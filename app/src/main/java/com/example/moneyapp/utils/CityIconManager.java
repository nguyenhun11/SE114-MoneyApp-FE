package com.example.moneyapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.mikepenz.iconics.IconicsDrawable;

public class CityIconManager {

    public static class CityIcon {
        public String iconName;
        public int color;

        public CityIcon(String iconName, int color) {
            this.iconName = iconName;
            this.color = color;
        }
    }

    public static CityIcon getBuildingInfo(String type, int level) {
        String iconName = "gmd_home";
        int color = Color.parseColor("#424242"); // Xám mặc định

        if (type == null) return new CityIcon("gmd_location_city", color);

        switch (type.toLowerCase()) {
            case "house":
                if (level >= 3) {
                    iconName = "gmd_domain"; // Biểu tượng biệt thự/dinh thự
                    color = Color.parseColor("#673AB7"); // Tím sang trọng
                } else if (level >= 2) {
                    iconName = "gmd_apartment"; // Biểu tượng chung cư
                    color = Color.parseColor("#FFC107"); // Vàng hiện đại
                } else {
                    iconName = "gmd_home"; // Biểu tượng nhà cấp 4
                    color = Color.parseColor("#795548"); // Nâu gỗ
                }
                break;

            case "shop":
                if (level >= 3) {
                    iconName = "gmd_local_mall"; // Trung tâm thương mại
                    color = Color.parseColor("#E91E63"); // Hồng trung tâm
                } else if (level >= 2) {
                    iconName = "gmd_shopping_basket"; // Siêu thị
                    color = Color.parseColor("#FF9800"); // Cam rực rỡ
                } else {
                    iconName = "gmd_store"; // Cửa hàng nhỏ
                    color = Color.parseColor("#8BC34A"); // Xanh lá
                }
                break;

            case "factory":
                if (level >= 3) {
                    iconName = "gmd_business"; // Tòa nhà công nghiệp lớn
                    color = Color.parseColor("#3F51B5"); // Xanh công nghiệp
                } else if (level >= 2) {
                    iconName = "gmd_settings"; // Bánh răng công nghiệp
                    color = Color.parseColor("#607D8B"); // Xám thép
                } else {
                    iconName = "gmd_build"; // Công cụ cơ bản
                    color = Color.parseColor("#B0BEC5"); // Xám bạc
                }
                break;

            case "park":
                if (level >= 3) {
                    iconName = "gmd_forest";
                    color = Color.parseColor("#1B5E20"); // Xanh đậm
                } else if (level >= 2) {
                    iconName = "gmd_nature_people";
                    color = Color.parseColor("#4CAF50"); // Xanh tự nhiên
                } else {
                    iconName = "gmd_park";
                    color = Color.parseColor("#81C784"); // Xanh non
                }
                break;

            case "road":
                iconName = "gmd_alt_route"; // Biểu tượng đường
                color = Color.parseColor("#78909C"); // Màu xám đường
                break;

            case "tree":
                iconName = "gmd_nature"; // Biểu tượng cây
                color = Color.parseColor("#43A047"); // Màu xanh lá cây
                break;

            case "fountain":
                iconName = "gmd_bubble_chart";
                color = Color.parseColor("#29B6F6");
                break;

            case "bench":
                iconName = "gmd_event_seat"; // Icon ghế
                color = Color.parseColor("#8D6E63"); // Màu nâu
                break;

            case "street_light":
                iconName = "gmd_highlight"; // Icon đèn
                color = Color.parseColor("#FBC02D"); // Màu vàng sáng
                break;

            case "flower_bed":
                iconName = "gmd_filter_vintage"; // Icon hoa
                color = Color.parseColor("#EC407A"); // Màu hồng hoa
                break;

            case "statue":
                iconName = "gmd_emoji_events"; // Icon cúp/tượng
                color = Color.parseColor("#FFD54F"); // Màu vàng gold
                break;
        }

        return new CityIcon(iconName, color);
    }

    public static Drawable getBuildingDrawable(Context context, String type, int level) {
        if (type == null) return null;
        
        // Chuyển đổi tên type "shop" thành "store" để khớp với tên file png của bạn
        String fileType = type.toLowerCase();
        if (fileType.equals("shop")) fileType = "store";
        
        // Thử tìm ảnh PNG theo định dạng ic_building_type_vX
        String resourceName = "ic_building_" + fileType + "_v" + level;
        int resId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        
        if (resId != 0) {
            // Trả về ảnh PNG nguyên bản (không tint màu)
            return ContextCompat.getDrawable(context, resId);
        } else {
            // Fallback: Nếu không có PNG thì dùng Icon font (có tint màu)
            CityIcon info = getBuildingInfo(type, level);
            IconicsDrawable drawable = new IconicsDrawable(context, info.iconName);
            drawable.setColorFilter(info.color, PorterDuff.Mode.SRC_IN);
            return drawable;
        }
    }
}
