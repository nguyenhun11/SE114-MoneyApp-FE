package com.example.moneyapp.utils;

import com.example.moneyapp.R;

import java.util.ArrayList;
import java.util.List;

public class ResourceMapper {
    public static class ResourceItem {
        public int id;
        public int resourceId; // R.color.xxx hoặc R.drawable.xxx

        public ResourceItem(int id, int resourceId) {
            this.id = id;
            this.resourceId = resourceId;
        }
    }
    // ==========================================
    // 1. BỘ QUẢN LÝ MÀU SẮC (COLORS)
    // ==========================================
    public static List<ResourceItem> getAvailableColors() {
        List<ResourceItem> colors = new ArrayList<>();
        colors.add(new ResourceItem(1, R.color.colorDanger)); // Đỏ
        colors.add(new ResourceItem(2, R.color.colorOnPrimary)); // Tím/Xanh
        colors.add(new ResourceItem(3, R.color.colorInfo)); // Xanh lá
        colors.add(new ResourceItem(4, R.color.colorWarning)); // Cam
        colors.add(new ResourceItem(5, R.color.colorSuccess)); // Xanh dương
        colors.add(new ResourceItem(6, R.color.colorEmpty)); // Xám
        // ... Thêm bao nhiêu màu tùy thích
        return colors;
    }

    public static int getColorResourceById(int id) {
        for (ResourceItem item : getAvailableColors()) {
            if (item.id == id) return item.resourceId;
        }
        return R.color.colorEmpty; // Màu mặc định nếu không tìm thấy
    }

    // ==========================================
    // 2. BỘ QUẢN LÝ BIỂU TƯỢNG (ICONS)
    // ==========================================
    public static List<ResourceItem> getAvailableIcons() {
        List<ResourceItem> icons = new ArrayList<>();
        // Lưu ý: Thay bằng các icon bạn đang có trong thư mục drawable
        icons.add(new ResourceItem(1, R.drawable.ic_account));
        icons.add(new ResourceItem(2, R.drawable.ic_transaction));
        icons.add(new ResourceItem(3, R.drawable.ic_plus));
        icons.add(new ResourceItem(4, R.drawable.ic_profile));
        // ... Thêm bao nhiêu icon tùy thích
        return icons;
    }

    public static int getIconResourceById(int id) {
        for (ResourceItem item : getAvailableIcons()) {
            if (item.id == id) return item.resourceId;
        }
        return R.drawable.ic_account; // Icon mặc định nếu không tìm thấy
    }
}
