package com.example.moneyapp.view.category;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.utils.AppResourceManager;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private List<Category> backupCategories;
    private boolean isEditMode = false;
    private OnCategoryClickListener listener;
    private OnCategoryLongClickListener longClickListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public interface OnCategoryLongClickListener {
        void onCategoryLongClick(Category category, View anchorView);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void setOnCategoryLongClickListener(OnCategoryLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        if (editMode) {
            this.backupCategories = new ArrayList<>(categories);
        }
        notifyDataSetChanged();
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void restoreBackup() {
        if (backupCategories != null) {
            this.categories = new ArrayList<>(backupCategories);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category, listener, longClickListener, isEditMode);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void updateData(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(categories, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(categories, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public List<Category> getCategories() {
        return categories;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final IconicsImageView ivIcon;
        private final TextView tvName;
        private final View viewColorCircle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
            viewColorCircle = itemView.findViewById(R.id.view_color_circle);
        }

        public void bind(Category category, OnCategoryClickListener listener, OnCategoryLongClickListener longClickListener, boolean isEditMode) {
            tvName.setText(category.getCategoryName());
            
            // Lấy màu thực tế từ ID thông qua AppResourceManager
            int colorValue = AppResourceManager.getColor(category.getColor());
            viewColorCircle.setBackgroundTintList(ColorStateList.valueOf(colorValue));
            viewColorCircle.setBackgroundResource(R.drawable.bg_circle);

            if (isEditMode) {
                itemView.setBackgroundResource(R.drawable.bg_category_item_edit);
            } else {
                itemView.setBackgroundResource(0);
            }
            
            // Lấy Resource ID của icon từ ID thông qua AppResourceManager
            String iconName = AppResourceManager.getIconName(category.getIcon());
            Context context = itemView.getContext();
            ivIcon.setIcon(new IconicsDrawable(context, iconName));

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCategoryClick(category);
            });

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onCategoryLongClick(category, v);
                    return true;
                }
                return false;
            });
        }
    }
}
