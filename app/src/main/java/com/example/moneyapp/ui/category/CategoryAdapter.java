package com.example.moneyapp.ui.category;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.entity.Category;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
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
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void updateData(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvName;
        private final View viewColorCircle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
            viewColorCircle = itemView.findViewById(R.id.view_color_circle);
        }

        public void bind(Category category, OnCategoryClickListener listener) {
            tvName.setText(category.getName());
            
            try {
                int color = Color.parseColor(category.getColor());
                // Set color cho hình tròn bên trong
                viewColorCircle.setBackgroundTintList(ColorStateList.valueOf(color));
                viewColorCircle.setBackgroundResource(R.drawable.bg_circle);
                
                int iconResId = itemView.getContext().getResources().getIdentifier(
                        category.getIcon(), "drawable", itemView.getContext().getPackageName());
                if (iconResId != 0) {
                    ivIcon.setImageResource(iconResId);
                }
            } catch (Exception e) {
                viewColorCircle.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCategoryClick(category);
            });
        }
    }
}
