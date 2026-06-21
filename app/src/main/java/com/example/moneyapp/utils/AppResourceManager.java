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
            "gmd_receipt",          // 0: Giao dịch
            "gmd_home",             // 1: Nhà
            "gmd_account_balance",  // 2: Ngân hàng/Ví
            "gmd_person",           // 3: Người dùng
            "gmd_insert_chart",     // 4: Thống kê
            "gmd_add",              // 5: Thêm
            "gmd_more_vert",        // 6: Thêm nữa
            "gmd_swap_horiz",       // 7: Chuyển khoản
            "gmd_arrow_back",       // 8: Quay lại
            "gmd_account_balance_wallet", // 9: Ví
            "gmd_shopping_cart",    // 10: Mua sắm
            "gmd_restaurant",       // 11: Ăn uống
            "gmd_directions_car",   // 12: Di chuyển
            "gmd_local_attraction", // 13: Giải trí
            "gmd_work",             // 14: Lương/Công việc
            "gmd_help_outline",     // 15: Khác
            // Icon cho Mục tiêu (Bắt đầu từ index 16)
            "gmd_star",             // 16
            "gmd_account_balance_wallet", // 17 (Thay gmd_savings bị lỗi)
            "gmd_directions_bike",  // 18
            "gmd_flight",           // 19
            "gmd_laptop",           // 20
            "gmd_smartphone",       // 21
            "gmd_favorite",         // 22
            "gmd_home",             // 23
            "gmd_directions_car"    // 24
    };

    public static int getGoalIconStart() {
        return 16;
    }

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
        return "gmd_receipt"; // Mặc định
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
