package com.example.moneyapp.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.PendingTransactionDao;
import com.example.moneyapp.data.local.entity.PendingTransaction;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.data.repository.CategoryRepository;
import com.example.moneyapp.data.repository.TransactionRepository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * BroadcastReceiver nhận sự kiện và xử lý các hành động nhanh khi người dùng
 * nhấn nút tương tác trên thông báo đẩy của hệ thống (Status bar notification).
 * Hai hành động được hỗ trợ:
 * - ACTION_QUICK_SAVE: Tự động tạo giao dịch chính thức và lưu vào Database, sau đó xóa bản nháp.
 * - ACTION_IGNORE: Xóa bản nháp giao dịch chờ duyệt và đóng thông báo.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    private static final int BASE_NOTIF_ID = 999;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String pendingTxId = intent.getStringExtra("PENDING_TX_ID");

        if (action == null || pendingTxId == null) {
            Log.w(TAG, "Không tìm thấy Action hoặc ID giao dịch nháp.");
            return;
        }

        // Tự động đóng thông báo đẩy tương ứng
        int notifId = BASE_NOTIF_ID + pendingTxId.hashCode();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(notifId);
        }

        Log.d(TAG, "Nhận Action: " + action + " cho giao dịch: " + pendingTxId);

        // Thực thi xử lý database trên luồng nền (Executor)
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
            PendingTransactionDao pendingDao = db.pendingTransactionDao();

            if ("ACTION_QUICK_SAVE".equals(action)) {
                handleQuickSave(context, db, pendingDao, pendingTxId);
            } else if ("ACTION_IGNORE".equals(action)) {
                handleIgnore(context, pendingDao, pendingTxId);
            }
        });
    }

    /**
     * Xử lý lưu nhanh giao dịch nháp thành giao dịch chính thức.
     */
    private void handleQuickSave(Context context, AppDatabase db, PendingTransactionDao pendingDao, String pendingTxId) {
        // 1. Tải giao dịch nháp từ SQLite
        PendingTransaction pendingTx = pendingDao.getPendingTransactionById(pendingTxId);
        if (pendingTx == null) {
            Log.e(TAG, "Không tìm thấy giao dịch chờ duyệt trong Database.");
            return;
        }

        // Lấy thông tin user hiện tại đang đăng nhập
        String userId = PreferenceManager.getInstance(context).getUserID();
        if (userId == null || userId.isEmpty()) {
            showToastOnMainThread(context, "Lỗi: Người dùng chưa đăng nhập!");
            return;
        }

        AccountRepository accountRepo = new AccountRepository(context);
        CategoryRepository categoryRepo = new CategoryRepository(context);
        TransactionRepository transactionRepo = new TransactionRepository(context);

        // Gọi API để lấy danh sách ví và lưu
        accountRepo.getAllAccounts(new AccountRepository.AccountCallback<List<Account>>() {
            @Override
            public void onSuccess(List<Account> accounts) {
                if (accounts.isEmpty()) {
                    showToastOnMainThread(context, "Lỗi: Không tìm thấy ví tài khoản nào để lưu!");
                    return;
                }

                Account matchedAccount = null;
                for (Account acc : accounts) {
                    if (acc.getAccountName().toLowerCase().contains(pendingTx.getAccountName().toLowerCase())) {
                        matchedAccount = acc;
                        break;
                    }
                }
                if (matchedAccount == null) {
                    matchedAccount = accounts.get(0);
                }

                final Account selectedAccount = matchedAccount;
                CategoryType categoryType = (pendingTx.getTransactionType() == 1) ? CategoryType.EXPENSE : CategoryType.INCOME;

                CategoryRepository.CategoryCallback<List<Category>> categoryCallback = new CategoryRepository.CategoryCallback<List<Category>>() {
                    @Override
                    public void onSuccess(List<Category> categories) {
                        if (categories.isEmpty()) {
                            showToastOnMainThread(context, "Lỗi: Không tìm thấy hạng mục phù hợp!");
                            return;
                        }

                        Category tempCategory = null;
                        for (Category cat : categories) {
                            if (cat.getCategoryName().equals("Khác") || cat.getCategoryName().equals("Khac")) {
                                tempCategory = cat;
                                break;
                            }
                        }
                        if (tempCategory == null) {
                            tempCategory = categories.get(0);
                        }

                        final Category matchedCategory = tempCategory;

                        Transaction officialTx = new Transaction(
                                UUID.randomUUID().toString(),
                                selectedAccount.getAccountId(),
                                selectedAccount.getAccountName(),
                                matchedCategory.getCategoryId(),
                                matchedCategory.getCategoryName(),
                                categoryType,
                                pendingTx.getAmount(),
                                selectedAccount.getCurrencyCode() != null ? selectedAccount.getCurrencyCode() : "VND",
                                0.0, 0.0, 1.0,
                                new Date(),
                                pendingTx.getNote(),
                                matchedCategory.getColor(),
                                matchedCategory.getIcon(),
                                selectedAccount.getColor(),
                                selectedAccount.getIcon(),
                                new ArrayList<>(),
                                0,
                                new Date()
                        );

                        transactionRepo.createTransaction(officialTx, new TransactionRepository.TransactionCallback<Transaction>() {
                            @Override
                            public void onSuccess(Transaction result) {
                                pendingDao.deletePendingTransaction(pendingTx);

                                // Gửi broadcast báo UI cập nhật
                                Intent updateIntent = new Intent("com.example.moneyapp.PENDING_TRANSACTION_UPDATED");
                                updateIntent.setPackage(context.getPackageName());
                                context.sendBroadcast(updateIntent);

                                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                                String formattedAmount = currencyFormat.format(pendingTx.getAmount());
                                showToastOnMainThread(context, "Đã lưu nhanh: " + formattedAmount + " (" + matchedCategory.getCategoryName() + ")");
                                Log.d(TAG, "Lưu nhanh giao dịch thành công. ID: " + result.getTransactionId());
                            }

                            @Override
                            public void onError(String message) {
                                showToastOnMainThread(context, "Lỗi khi lưu giao dịch: " + message);
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        showToastOnMainThread(context, "Lỗi tải hạng mục: " + message);
                    }
                };

                if (categoryType == CategoryType.EXPENSE) {
                    categoryRepo.getExpenseCategories(categoryCallback);
                } else {
                    categoryRepo.getIncomeCategories(categoryCallback);
                }
            }

            @Override
            public void onError(String message) {
                showToastOnMainThread(context, "Lỗi tải ví: " + message);
            }
        });
    }

    /**
     * Xử lý bỏ qua thông báo và xóa giao dịch nháp.
     */
    private void handleIgnore(Context context, PendingTransactionDao pendingDao, String pendingTxId) {
        try {
            pendingDao.deletePendingTransactionById(pendingTxId);
            Log.d(TAG, "Đã bỏ qua và xóa giao dịch nháp ID: " + pendingTxId);

            // Gửi broadcast báo UI cập nhật
            Intent updateIntent = new Intent("com.example.moneyapp.PENDING_TRANSACTION_UPDATED");
            updateIntent.setPackage(context.getPackageName());
            context.sendBroadcast(updateIntent);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xóa giao dịch nháp bỏ qua", e);
        }
    }

    /**
     * Phương thức tiện ích để hiển thị Toast từ một luồng nền lên Main Thread.
     */
    private void showToastOnMainThread(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() -> 
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }
}

