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
import com.example.moneyapp.data.remote.response.BadgeResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

public class RewardHelper {

    public static void showSmallReward(View anchor, String message) {
        if (anchor == null) return;
        
        vibrate(anchor.getContext(), 50); // Rung nhẹ 50ms

        // Đã chuyển từ Snackbar sang Dialog để thống nhất toàn bộ hệ thống thông báo
        DialogHelper.showSimpleDialog(anchor.getContext(), "Phần thưởng", message);
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

    public static void showBadgeDetail(Context context, BadgeResponse badge) {
        if (context == null || badge == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_badge_detail, null);

        IconicsImageView ivIcon = view.findViewById(R.id.iv_badge_icon_detail);
        View viewGlow = view.findViewById(R.id.view_badge_glow);
        TextView tvName = view.findViewById(R.id.tv_badge_name_detail);
        TextView tvDesc = view.findViewById(R.id.tv_badge_desc_detail);
        TextView tvStatus = view.findViewById(R.id.tv_badge_status_detail);

        tvName.setText(badge.getName());
        tvDesc.setText(badge.getDescription());

        if (badge.isUnlocked()) {
            String iconKey = badge.getIconKey() != null ? badge.getIconKey() : "gmd_stars";
            ivIcon.setIcon(new IconicsDrawable(context, iconKey));
            ivIcon.setColorFilter(android.graphics.Color.parseColor("#FFC107"));
            viewGlow.setVisibility(View.VISIBLE);
            tvStatus.setText("Đã đạt được vào: " + DateConverter.formatToDisplay(badge.getUnlockedAt()));
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess));
        } else {
            ivIcon.setIcon(new IconicsDrawable(context, "gmd_lock"));
            ivIcon.setColorFilter(android.graphics.Color.parseColor("#BDBDBD"));
            viewGlow.setVisibility(View.GONE);
            tvStatus.setText("Hãy cố gắng để mở khóa!");
            tvStatus.setTextColor(android.graphics.Color.parseColor("#BDBDBD"));
        }

        view.findViewById(R.id.btn_badge_close).setOnClickListener(v -> dialog.dismiss());

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
