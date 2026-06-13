package com.example.moneyapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.IconicsSize;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.IconicsSize;

public class AppResourceManager {

    // Bảng màu cố định tại Frontend
    private static final String[] COLOR_PALETTE = {
            "#FF7F3DFF", // 0: Tím chính (Primary)
            "#FF0077FF", // 1: Xanh dương
            "#FF00A86B", // 2: Xanh lá
            "#FFFD3C4A", // 3: Đỏ
            "#FFFCAC12", // 4: Vàng/Cam
            "#FF919191", // 5: Xám
            "#FF212325", // 6: Đen
            "#FF7B61FF", // 7: Tím sáng
            "#FF29B6F6", // 8: Xanh trời
            "#FFEC407A", // 9: Hồng
            "#FFA726",   // 10: Cam đất
            "#FF4CAF50"  // 11: Xanh lá đậm
    };

    // Danh sách các Icon khả dụng (Format gmd-xxx cho Iconics)
    private static final String[] ICON_LIST = {
            "gmd-receipt",          // 0: Giao dịch
            "gmd-home",             // 1: Nhà
            "gmd-account-balance",  // 2: Ngân hàng/Ví
            "gmd-person",           // 3: Người dùng
            "gmd-insert-chart",     // 4: Thống kê
            "gmd-add",              // 5: Thêm
            "gmd-more-vert",        // 6: Thêm nữa
            "gmd-swap-horiz",       // 7: Chuyển khoản
            "gmd-arrow-back",       // 8: Quay lại
            "gmd-account-balance-wallet", // 9: Ví
            "gmd-shopping-cart",    // 10: Mua sắm
            "gmd-restaurant",       // 11: Ăn uống
            "gmd-directions-car",   // 12: Di chuyển
            "gmd-local-attraction", // 13: Giải trí
            "gmd-work"              // 14: Lương/Công việc
    };

    /**
     * Lấy mã màu từ Color ID (index)
     */
    public static int getColor(int colorId) {
        if (colorId >= 0 && colorId < COLOR_PALETTE.length) {
            return Color.parseColor(COLOR_PALETTE[colorId]);
        }
        return Color.parseColor("#FF919191"); // Mặc định xám
    }

    /**
     * Lấy tên Icon cho Android-Iconics
     */
    public static String getIconName(int iconId) {
        if (iconId >= 0 && iconId < ICON_LIST.length) {
            return ICON_LIST[iconId];
        }
        return "gmd-receipt"; // Mặc định
    }

    // =========================================================
    // CÁC HÀM TIỆN ÍCH TRẢ VỀ TRỰC TIẾP ICON (CHỐNG MÀU ĐEN)
    // =========================================================

    /**
     * Trả về một Icon đã được nhuộm màu tùy ý
     * @param context Môi trường hiện tại (requireContext(), this, itemView.getContext())
     * @param iconId ID của icon trong mảng
     * @param colorInt Giá trị màu thực tế (Color.WHITE, hoặc lấy từ getColor())
     */
    public static Drawable getIconDrawable(Context context, int iconId, int colorInt) {
        String iconName = getIconName(iconId);
        IconicsDrawable drawable = new IconicsDrawable(context, iconName);
        drawable.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN);

        int paddingPx = (int) (3 * context.getResources().getDisplayMetrics().density);
        return new InsetDrawable(drawable, paddingPx);
    }

    public static Drawable getWhiteIcon(Context context, int iconId) {
        return getIconDrawable(context, iconId, Color.WHITE);
    }

    public static Drawable getBlackIcon(Context context, int iconId) {
        return getIconDrawable(context, iconId, Color.BLACK);
    }

    public static int getColorCount() {
        return COLOR_PALETTE.length;
    }

    public static int getIconCount() {
        return ICON_LIST.length;
    }
}
