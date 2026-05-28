package com.example.moneyapp.utils;

import android.graphics.Color;
import com.example.moneyapp.R;

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

    // Danh sách các Icon khả dụng
    private static final int[] ICON_LIST = {
            R.drawable.ic_transaction, // 0
            R.drawable.ic_home,        // 1
            R.drawable.ic_account,     // 2
            R.drawable.ic_profile,     // 3
            R.drawable.ic_statistics,  // 4
            R.drawable.ic_plus,        // 5
            R.drawable.ic_more,        // 6
            R.drawable.ic_transfer,    // 7
            R.drawable.ic_back         // 8
    };

    /**
     * Lấy mã màu từ Color ID (index)
     * @param colorId ID màu từ API/Database
     * @return Giá trị màu (int)
     */
    public static int getColor(int colorId) {
        if (colorId >= 0 && colorId < COLOR_PALETTE.length) {
            return Color.parseColor(COLOR_PALETTE[colorId]);
        }
        return Color.parseColor("#FF919191"); // Mặc định xám
    }

    /**
     * Lấy Resource ID của Icon từ Icon ID (index)
     * @param iconId ID icon từ API/Database
     * @return Resource ID của drawable
     */
    public static int getIconRes(int iconId) {
        if (iconId >= 0 && iconId < ICON_LIST.length) {
            return ICON_LIST[iconId];
        }
        return R.drawable.ic_transaction; // Mặc định
    }
}
