package com.example.moneyapp.utils;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

public class DialogHelper {

    /**
     * Hiển thị một Dialog đơn giản chỉ có nút OK.
     */
    public static void showSimpleDialog(Context context, String title, String message) {
        showSimpleDialog(context, title, message, null);
    }

    /**
     * Hiển thị một Dialog đơn giản có nút OK và callback khi đóng.
     */
    public static void showSimpleDialog(Context context, String title, String message, Runnable onOk) {
        if (context == null) return;

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (onOk != null) onOk.run();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * Hiển thị Dialog xác nhận với hai nút Xác nhận và Hủy.
     */
    public static void showConfirmDialog(Context context, String title, String message, 
                                          Runnable onConfirm, Runnable onCancel) {
        if (context == null) return;

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    if (onConfirm != null) onConfirm.run();
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    if (onCancel != null) onCancel.run();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .create()
                .show();
    }
}
