package com.example.moneyapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class PopupHelper {

    public interface OnResourceSelectedListener {
        void onSelected(int id);
    }

    // ==========================================
    // MỞ POPUP CHỌN MÀU
    // ==========================================
    public static void showColorPicker(Context context, OnResourceSelectedListener listener) {
        showPicker(context, ResourceMapper.getAvailableColors(), true, listener);
    }

    // ==========================================
    // MỞ POPUP CHỌN ICON
    // ==========================================
    public static void showIconPicker(Context context, OnResourceSelectedListener listener) {
        showPicker(context, ResourceMapper.getAvailableIcons(), false, listener);
    }

    // Hàm lõi xử lý chung cho cả 2 loại Popup
    private static void showPicker(Context context, List<ResourceMapper.ResourceItem> items, boolean isColorPicker, OnResourceSelectedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);

        // Tạo RecyclerView bằng code thay vì XML để tiết kiệm file
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setPadding(32, 32, 32, 32);
        recyclerView.setClipToPadding(false);

        // Hiển thị dạng lưới (Grid) 5 cột
        recyclerView.setLayoutManager(new GridLayoutManager(context, 5));

        // Tạo Adapter nhanh
        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                int layoutId = isColorPicker ? R.layout.item_picker_color : R.layout.item_picker_icon;
                View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ResourceMapper.ResourceItem item = items.get(position);

                if (isColorPicker) {
                    View colorCircle = holder.itemView.findViewById(R.id.v_color_circle);
                    int actualColor = ContextCompat.getColor(context, item.resourceId);
                    colorCircle.getBackground().setTint(actualColor);
                } else {
                    ImageView ivIcon = holder.itemView.findViewById(R.id.iv_icon);
                    ivIcon.setImageResource(item.resourceId);
                }

                // Xử lý sự kiện Click
                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(item.id);
                    dialog.dismiss(); // Tự động đóng popup sau khi chọn
                });
            }

            @Override
            public int getItemCount() {
                return items.size();
            }
        };

        recyclerView.setAdapter(adapter);
        dialog.setContentView(recyclerView);
        dialog.show();
    }
}