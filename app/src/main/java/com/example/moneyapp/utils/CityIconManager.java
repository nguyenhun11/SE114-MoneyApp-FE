package com.example.moneyapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

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
                    iconName = "gmd_domain";
                    color = Color.parseColor("#673AB7"); // Tím sang trọng
                } else if (level >= 2) {
                    iconName = "gmd_apartment";
                    color = Color.parseColor("#FFC107"); // Vàng hiện đại
                } else {
                    iconName = "gmd_home";
                    color = Color.parseColor("#795548"); // Nâu gỗ
                }
                break;

            case "shop":
                if (level >= 3) {
                    iconName = "gmd_local_mall";
                    color = Color.parseColor("#E91E63"); // Hồng trung tâm
                } else if (level >= 2) {
                    iconName = "gmd_store_mall_directory";
                    color = Color.parseColor("#FF9800"); // Cam rực rỡ
                } else {
                    iconName = "gmd_store";
                    color = Color.parseColor("#8BC34A"); // Xanh lá
                }
                break;

            case "factory":
                if (level >= 3) {
                    iconName = "gmd_business"; // Thay thế precision_manufacturing
                    color = Color.parseColor("#3F51B5"); // Xanh công nghiệp
                } else if (level >= 2) {
                    iconName = "gmd_build"; // Thay thế engineering
                    color = Color.parseColor("#607D8B"); // Xám thép
                } else {
                    iconName = "gmd_account_balance"; // Thay thế factory nếu không có
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
        }

        return new CityIcon(iconName, color);
    }

    public static Drawable getBuildingDrawable(Context context, String type, int level) {
        CityIcon info = getBuildingInfo(type, level);
        IconicsDrawable drawable = new IconicsDrawable(context, info.iconName);
        drawable.setColorFilter(info.color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }
}
