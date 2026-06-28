package com.example.moneyapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.entity.PendingTransaction;
import com.example.moneyapp.data.repository.PendingTransactionRepository;
import com.example.moneyapp.view.MainActivity;
import com.example.moneyapp.data.local.PreferenceManager;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service chạy ngầm trong hệ thống Android kế thừa NotificationListenerService.
 * Dùng để lắng nghe toàn bộ thông báo hiển thị trên thiết bị.
 * Khi nhận được thông báo từ các nguồn ngân hàng/SMS hoặc cú pháp test,
 * service sẽ bóc tách dữ liệu giao dịch và lưu vào danh sách chờ duyệt (PendingTransaction).
 */
public class TransactionNotificationListenerService extends NotificationListenerService {

    private static final String TAG = "NotificationListener";
    private static final String CHANNEL_ID = "moneyapp_auto_transactions";
    private static final String CHANNEL_NAME = "Giao dịch tự động";
    private static final int BASE_NOTIF_ID = 999;

    private PendingTransactionRepository pendingRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo repository để thao tác với cơ sở dữ liệu
        pendingRepository = new PendingTransactionRepository(getApplication());
        Log.d(TAG, "TransactionNotificationListenerService đã được khởi tạo.");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Kiểm tra xem tính năng tự động ghi chép đã được người dùng bật trong Settings chưa
        boolean isEnabled = PreferenceManager.getInstance(getApplicationContext()).isLoggedIn(); // kiểm tra đăng nhập
        // Đọc giá trị cấu hình cài đặt của tính năng lắng nghe thông báo
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(getApplicationContext());
        if (!prefs.isListenerEnabled()) {
            Log.d(TAG, "Tính năng đọc thông báo đang tắt.");
            return;
        }

        // Lấy thông tin chi tiết của thông báo hệ thống nhận được
        String packageName = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        if (extras == null) return;

        // Trích xuất tiêu đề (Title) và nội dung (Content Text) của thông báo
        String title = extras.getString(Notification.EXTRA_TITLE, "");
        CharSequence textChar = extras.getCharSequence(Notification.EXTRA_TEXT);
        String text = (textChar != null) ? textChar.toString() : "";

        Log.d(TAG, "Nhận thông báo từ: " + packageName + " | Title: " + title + " | Text: " + text);

        // Bắt đầu quy trình kiểm tra và bóc tách dữ liệu giao dịch
        parseAndSaveTransaction(packageName, title, text);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d(TAG, "Thông báo bị xóa khỏi thanh trạng thái: " + sbn.getPackageName());
    }

    /**
     * Phương thức phân tích nội dung thông báo để tìm kiếm và trích xuất thông tin giao dịch.
     * 
     * @param packageName Tên package của app gửi thông báo
     * @param title Tiêu đề thông báo
     * @param text Nội dung văn bản thông báo
     */
    private void parseAndSaveTransaction(String packageName, String title, String text) {
        // Nếu tiêu đề hoặc nội dung rỗng thì bỏ qua
        if (text.isEmpty()) return;

        double amount = 0.0;
        int transactionType = 1; // 1: Chi tiêu (mặc định), 2: Thu nhập
        String note = "";
        String accountName = "Ngân hàng"; // Tên ví nguồn mặc định nhận diện
        boolean matched = false;

        // 1. KIỂM THỬ GIẢ LẬP (Cú pháp test đặc biệt của MoneyApp)
        // Định dạng: "MoneyApp Test -150000 VND mua do an" hoặc "MoneyApp Test +200000 VND luong ve"
        if (text.contains("MoneyApp Test") || title.contains("MoneyApp Test")) {
            // Regex tìm dấu + hoặc -, theo sau là số tiền, chữ VND/đ và nội dung ghi chú phía sau
            Pattern pattern = Pattern.compile("MoneyApp Test\\s+([+-])\\s*(\\d+(?:[.,]\\d+)*)\\s*(?:VND|đ|d)?\\s*(.*)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String sign = matcher.group(1);
                String amountStr = matcher.group(2);
                note = matcher.group(3);

                // Loại giao dịch
                transactionType = "-".equals(sign) ? 1 : 2;
                // Chuẩn hóa số tiền
                amount = parseAmountString(amountStr);
                accountName = "Tiền mặt"; // Gán ví mặc định cho tài khoản test là Tiền mặt
                matched = true;
                Log.d(TAG, "Test notification khớp! Số tiền: " + amount + " | Loại: " + transactionType + " | Ghi chú: " + note);
            }
        }

        // 2. NHẬN DIỆN MẪU TIN NHẮN MOMO (Package: com.mservice.momo)
        if (!matched && (packageName.contains("momo") || text.toLowerCase().contains("momo"))) {
            accountName = "Momo";
            // Trường hợp 1: Nhận tiền (Thu nhập) -> "Ban da nhan 50.000đ tu..."
            if (text.toLowerCase().contains("nhận") || text.toLowerCase().contains("nhan")) {
                Pattern pattern = Pattern.compile("(?:nhận|nhan)\\s+(\\d+(?:[.,]\\d+)*)\\s*(?:đ|d|VND)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    amount = parseAmountString(matcher.group(1));
                    transactionType = 2; // Thu nhập
                    note = text; // Lấy toàn bộ text làm ghi chú tạm thời
                    matched = true;
                }
            }
            // Trường hợp 2: Thanh toán/Chuyển tiền (Chi tiêu) -> "Giao dich thanh cong. Ban da thanh toan 120.000đ cho..."
            else if (text.toLowerCase().contains("thanh toan") || text.toLowerCase().contains("gửi") || text.toLowerCase().contains("chuyển") || text.toLowerCase().contains("chuyen")) {
                Pattern pattern = Pattern.compile("(?:thanh toan|chuyển|chuyen|gửi|gui)\\s+(\\d+(?:[.,]\\d+)*)\\s*(?:đ|d|VND)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    amount = parseAmountString(matcher.group(1));
                    transactionType = 1; // Chi tiêu
                    note = text;
                    matched = true;
                }
            }
        }

        // 3. NHẬN DIỆN MẪU SMS / NGÂN HÀNG CHUNG (VCB, Techcombank, MB Bank...)
        // Thường có dạng: "TK ... GD: -100,000 VND" hoặc "+500,000 VND"
        if (!matched) {
            // Nhận diện ngân hàng cụ thể dựa trên text hoặc package name để hiển thị đúng tên ví nguồn
            if (text.contains("VCB") || text.contains("Vietcombank")) {
                accountName = "Ngân hàng"; // Tương ứng ví mặc định "Ngân hàng"
            } else if (text.contains("TCB") || text.contains("Techcombank")) {
                accountName = "Ngân hàng";
            } else if (text.contains("MB") || text.contains("MBBank")) {
                accountName = "Ngân hàng";
            }

            // Regex bắt biến động số dư dạng +/- Số_tiền VND/đ
            // VD: "-50.000 VND", "+1,500,000đ"
            Pattern pattern = Pattern.compile("([+-])\\s*(\\d+(?:[.,]\\d+)*)\\s*(?:VND|VND|đ|d)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String sign = matcher.group(1);
                String amountStr = matcher.group(2);
                
                amount = parseAmountString(amountStr);
                transactionType = "-".equals(sign) ? 1 : 2;
                note = text; // Lấy toàn bộ nội dung tin nhắn biến động làm ghi chú
                matched = true;
            }
        }

        // NẾU KHỚP GIAO DỊCH, THỰC HIỆN LƯU VÀO DATABASE VÀ HIỂN THỊ THÔNG BÁO ĐẨY
        if (matched && amount > 0) {
            final double finalAmount = amount;
            final int finalType = transactionType;
            final String finalNote = note.length() > 100 ? note.substring(0, 97) + "..." : note; // Giới hạn độ dài ghi chú
            final String finalAccountName = accountName;

            // Tạo đối tượng Giao dịch chờ duyệt mới
            PendingTransaction pendingTx = new PendingTransaction(finalType, finalAmount, finalNote, finalAccountName);

            // Lưu vào SQLite local thông qua Repository
            pendingRepository.addPendingTransaction(pendingTx, new PendingTransactionRepository.ActionCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Đã lưu giao dịch chờ duyệt thành công vào database.");
                    // Hiển thị thông báo đẩy của MoneyApp trên status bar để người dùng nhấn vào duyệt
                    showLocalNotification(pendingTx);
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "Lỗi khi lưu giao dịch chờ duyệt: " + message);
                }
            });
        }
    }

    /**
     * Phương thức chuyển đổi chuỗi số tiền chứa dấu phẩy hoặc dấu chấm phân cách thành kiểu double.
     * Ví dụ: "150.000" -> 150000.0, "1,500,000" -> 1500000.0
     */
    private double parseAmountString(String amountStr) {
        try {
            // Loại bỏ tất cả các ký tự không phải số (loại bỏ dấu chấm và dấu phẩy ngăn cách hàng nghìn)
            String cleanStr = amountStr.replaceAll("[.,]", "");
            return Double.parseDouble(cleanStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi định dạng số tiền: " + amountStr, e);
            return 0.0;
        }
    }

    /**
     * Phương thức hiển thị một thông báo đẩy của MoneyApp trên thanh trạng thái.
     * Thông báo đẩy này cho phép người dùng click để mở app hoặc chọn nhanh "Lưu nhanh" / "Bỏ qua".
     * 
     * @param pendingTx Đối tượng giao dịch nháp vừa được lưu
     */
    private void showLocalNotification(PendingTransaction pendingTx) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        // Khởi tạo Notification Channel nếu chạy trên Android O (8.0) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Kênh thông báo cho các giao dịch tự động phát hiện");
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Định dạng hiển thị số tiền tiếng Việt
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedAmount = currencyFormat.format(pendingTx.getAmount());

        String typeStr = (pendingTx.getTransactionType() == 1) ? "Chi tiêu" : "Thu nhập";
        String titleStr = "Phát hiện giao dịch " + typeStr + " tự động";
        String contentStr = "[" + pendingTx.getAccountName() + "] " + formattedAmount + " - " + pendingTx.getNote();

        // 1. Tạo Intent mở MainActivity -> Chuyển hướng trực tiếp vào màn hình Danh sách giao dịch chờ duyệt
        Intent openIntent = new Intent(this, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        openIntent.putExtra("OPEN_PENDING_LIST", true); // Extra báo cho MainActivity biết cần mở màn hình duyệt

        // Cần FLAG_IMMUTABLE hoặc FLAG_UPDATE_CURRENT tùy thuộc yêu cầu Android SDK mới
        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntentFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, pendingTx.getId().hashCode(), openIntent, pendingIntentFlags);

        // 2. Nút hành động nhanh "LƯU NHANH" (Quick Save) trên thông báo đẩy
        Intent quickSaveIntent = new Intent(this, NotificationReceiver.class);
        quickSaveIntent.setAction("ACTION_QUICK_SAVE");
        quickSaveIntent.putExtra("PENDING_TX_ID", pendingTx.getId());
        
        int broadcastFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            broadcastFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent quickSavePendingIntent = PendingIntent.getBroadcast(this, pendingTx.getId().hashCode() + 1, quickSaveIntent, broadcastFlags);

        // 3. Nút hành động "BỎ QUA" (Ignore) trên thông báo đẩy
        Intent ignoreIntent = new Intent(this, NotificationReceiver.class);
        ignoreIntent.setAction("ACTION_IGNORE");
        ignoreIntent.putExtra("PENDING_TX_ID", pendingTx.getId());
        PendingIntent ignorePendingIntent = PendingIntent.getBroadcast(this, pendingTx.getId().hashCode() + 2, ignoreIntent, broadcastFlags);

        // Build giao diện thông báo đẩy
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // Icon ứng dụng mặc định
                .setContentTitle(titleStr)
                .setContentText(contentStr)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentStr)) // Hỗ trợ hiển thị tin nhắn dài đầy đủ
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true) // Tự động xóa thông báo khi click vào
                .setContentIntent(openPendingIntent) // Click vào thông báo sẽ mở màn hình duyệt
                .addAction(android.R.drawable.ic_input_add, "Lưu nhanh", quickSavePendingIntent) // Thêm nút Lưu nhanh
                .addAction(android.R.drawable.ic_delete, "Bỏ qua", ignorePendingIntent); // Thêm nút Bỏ qua

        // Sử dụng mã hash của UUID để làm notification id duy nhất, tránh ghi đè thông báo nếu có nhiều giao dịch liên tiếp
        int notifId = BASE_NOTIF_ID + pendingTx.getId().hashCode();
        notificationManager.notify(notifId, builder.build());
    }

    /**
     * Lớp helper phụ trợ đọc trạng thái SharedPreferences của tính năng lắng nghe thông báo.
     * Được khai báo ở đây để tránh bị lỗi import hoặc reference.
     */
    private static class SharedPreferencesHelper {
        private final SharedPreferences sharedPreferences;

        public SharedPreferencesHelper(Context context) {
            this.sharedPreferences = context.getSharedPreferences("MoneyAppPrefs", Context.MODE_PRIVATE);
        }

        public boolean isListenerEnabled() {
            // Giá trị mặc định là false, người dùng phải vào cài đặt bật lên
            return sharedPreferences.getBoolean("isNotificationListenerEnabled", false);
        }
    }
}
