package com.example.moneyapp.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.moneyapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

public class RewardHelper {

    public static void showSmallReward(View anchor, String message) {
        if (anchor == null) return;
        
        vibrate(anchor.getContext(), 50); // Rung nhẹ 50ms

        Snackbar snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(anchor.getContext(), R.color.colorInfo));
        snackbar.setTextColor(ContextCompat.getColor(anchor.getContext(), R.color.white));
        snackbar.show();
    }

    public static void showBigReward(Context context, String points, String message) {
        if (context == null) return;

        vibrate(context, 100); // Rung mạnh hơn chút cho phần thưởng lớn

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_reward_dialog, null);

        TextView tvPoints = view.findViewById(R.id.tv_reward_points);
        TextView tvMsg = view.findViewById(R.id.tv_reward_message);
        
        tvPoints.setText(points);
        tvMsg.setText(message);

        view.findViewById(R.id.btn_reward_close).setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }

    private static void vibrate(Context context, long duration) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(duration);
            }
        }
    }
}
