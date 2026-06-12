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
import com.example.moneyapp.view.transaction.AccountQuickAdapter;
import com.example.moneyapp.view.transaction.CategoryQuickAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.iconics.view.IconicsImageView;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.List;

public class PopupHelper {

    public interface OnResourceSelectedListener {
        void onSelected(int id);
    }

    public static void showColorPicker(Context context, OnResourceSelectedListener listener) {
        showPicker(context, true, listener);
    }

    public static void showIconPicker(Context context, OnResourceSelectedListener listener) {
        showPicker(context, false, listener);
    }

    // Hàm lõi xử lý chung cho cả 2 loại Popup
    public static void showPicker(Context context, boolean isColorPicker, OnResourceSelectedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        recyclerView.setLayoutManager(new GridLayoutManager(context, 5));

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public int getItemCount() {
                return isColorPicker ? AppResourceManager.getColorCount() : AppResourceManager.getIconCount();
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Layout của cậu phải đổi thẻ ImageView thành com.mikepenz.iconics.view.IconicsImageView
                int layoutId = isColorPicker ? R.layout.item_picker_color : R.layout.item_picker_icon;
                View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (isColorPicker) {
                    View colorCircle = holder.itemView.findViewById(R.id.v_color_circle);
                    colorCircle.getBackground().setTint(AppResourceManager.getColor(position));
                } else {
                    // ĐÂY LÀ PHẦN QUAN TRỌNG NHẤT
                    IconicsImageView ivIcon = holder.itemView.findViewById(R.id.iv_icon);
                    String iconName = AppResourceManager.getIconName(position);

                    ivIcon.setIcon(new com.mikepenz.iconics.IconicsDrawable(context, iconName));
                    ivIcon.setColorFilter(android.graphics.Color.BLACK); // Nhuộm màu icon trong popup
                }

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(position); // Trả về index
                    dialog.dismiss();
                });
            }
        };

        recyclerView.setAdapter(adapter);
        dialog.setContentView(recyclerView);
        dialog.show();
    }

    // ==========================================
    // MỞ POPUP CHỌN TÀI KHOẢN (Tái sử dụng AccountQuickAdapter)
    // ==========================================
    public static void showAccountFilterPopup(Context context, List<com.example.moneyapp.model.Account> accountList, AccountQuickAdapter.OnAccountClickListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setPadding(24, 48, 24, 48); // Padding cho thoáng
        recyclerView.setClipToPadding(false);

        // Hiển thị dạng lưới 4 cột cho đẹp
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));

        // Tái sử dụng Adapter đã tạo
        AccountQuickAdapter adapter = new AccountQuickAdapter(accountList, account -> {
            if (listener != null) listener.onAccountClick(account);
            dialog.dismiss(); // Tự đóng sau khi chọn
        });

        recyclerView.setAdapter(adapter);
        dialog.setContentView(recyclerView);
        dialog.show();
    }

    // ==========================================
    // MỞ POPUP CHỌN HẠNG MỤC (Tái sử dụng CategoryQuickAdapter)
    // ==========================================
    public static void showCategoryFilterPopup(Context context, List<com.example.moneyapp.model.Category> categoryList, CategoryQuickAdapter.OnCategoryClickListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setPadding(24, 48, 24, 48);
        recyclerView.setClipToPadding(false);

        // Hiển thị dạng lưới 4 cột
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));

        CategoryQuickAdapter adapter = new CategoryQuickAdapter(categoryList, category -> {
            if (listener != null) listener.onCategoryClick(category);
            dialog.dismiss();
        });

        recyclerView.setAdapter(adapter);
        dialog.setContentView(recyclerView);
        dialog.show();
    }

}