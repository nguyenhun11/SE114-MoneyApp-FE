package com.example.moneyapp.view.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.view.BaseFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Set;

/**
 * Fragment hiển thị giao diện cấu hình Cài đặt của MoneyApp.
 * Quản lý tính năng bật/tắt Lắng nghe thông báo tự động (Notification Listener).
 * Hướng dẫn người dùng cấp quyền hệ thống nếu quyền chưa được kích hoạt.
 */
public class SettingFragment extends BaseFragment {

    private SwitchMaterial switchNotification;
    private PreferenceManager preferenceManager;
    private boolean isCheckingPermission = false; // Cờ theo dõi khi người dùng quay lại từ màn hình cài đặt hệ thống

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo PreferenceManager dùng chung
        preferenceManager = PreferenceManager.getInstance(requireContext().getApplicationContext());

        // Thiết lập header tiêu đề và nút quay lại
        setupHeader(view, "Cài đặt", true);

        // Ánh xạ SwitchMaterial điều khiển tính năng đọc thông báo
        switchNotification = view.findViewById(R.id.switch_notification_listener);

        // Cập nhật trạng thái hiển thị của Switch dựa trên giá trị đã lưu
        boolean isFeatureEnabled = preferenceManager.isNotificationListenerEnabled();
        switchNotification.setChecked(isFeatureEnabled);

        // Đăng ký sự kiện thay đổi trạng thái bật/tắt của Switch
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Nếu người dùng bật: Kiểm tra xem quyền truy cập thông báo của hệ thống đã được cấp chưa
                if (isNotificationServiceEnabled()) {
                    // Nếu đã có quyền, lưu trạng thái bật vào preferences
                    preferenceManager.setNotificationListenerEnabled(true);
                    Toast.makeText(getContext(), "Đã kích hoạt tính năng tự động đọc thông báo!", Toast.LENGTH_SHORT).show();
                    
                    // Kích hoạt kết nối dịch vụ
                    android.service.notification.NotificationListenerService.requestRebind(
                            new android.content.ComponentName(requireContext(), com.example.moneyapp.service.TransactionNotificationListenerService.class)
                    );
                } else {
                    // Nếu chưa có quyền, tắt tạm thời switch và hiển thị Dialog giải thích & hướng dẫn cấp quyền
                    switchNotification.setChecked(false);
                    showPermissionExplanationDialog();
                }
            } else {
                // Nếu người dùng chủ động tắt: lưu trạng thái tắt vào preferences
                preferenceManager.setNotificationListenerEnabled(false);
                Toast.makeText(getContext(), "Đã tắt tính năng tự động đọc thông báo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Khi người dùng quay lại từ màn hình Cài đặt hệ thống, kiểm tra lại xem họ đã bật quyền chưa
        if (isCheckingPermission) {
            isCheckingPermission = false;
            if (isNotificationServiceEnabled()) {
                // Nếu đã bật thành công trong cài đặt hệ thống, cập nhật UI và Preference
                switchNotification.setChecked(true);
                preferenceManager.setNotificationListenerEnabled(true);
                Toast.makeText(getContext(), "Cấp quyền thành công! Đã bật tự động đọc thông báo.", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu vẫn chưa cấp quyền
                switchNotification.setChecked(false);
                preferenceManager.setNotificationListenerEnabled(false);
                Toast.makeText(getContext(), "Tính năng yêu cầu quyền truy cập thông báo để hoạt động.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Kiểm tra xem ứng dụng MoneyApp đã được cấp quyền lắng nghe thông báo hệ thống chưa.
     */
    private boolean isNotificationServiceEnabled() {
        Context context = requireContext();
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        return packageNames.contains(context.getPackageName());
    }

    /**
     * Hiển thị Dialog giải thích rõ lý do cần quyền truy cập thông báo.
     */
    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Yêu cầu quyền truy cập thông báo")
                .setMessage("Để tự động phát hiện và bóc tách các giao dịch từ MoMo, SMS hoặc thông báo biến động số dư ngân hàng, ứng dụng cần bạn cấp quyền 'Truy cập thông báo' trong phần cài đặt của điện thoại.\n\nMoneyApp cam kết chỉ đọc các thông báo giao dịch tài chính liên quan và bảo vệ tuyệt đối dữ liệu cá nhân của bạn.")
                .setPositiveButton("Đi đến Cài đặt", (dialog, which) -> {
                    isCheckingPermission = true;
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy bỏ", (dialog, which) -> {
                    dialog.dismiss();
                    switchNotification.setChecked(false);
                })
                .setCancelable(false)
                .show();
    }
}

