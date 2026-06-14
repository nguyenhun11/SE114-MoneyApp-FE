package com.example.moneyapp.view.category;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Chuyển sang dùng ImageView thường
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.utils.AppResourceManager;

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
        private final ImageView ivIcon; // Sửa 'Draw' thành 'ImageView'
        private final TextView tvName;
        private final View viewColorCircle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
            viewColorCircle = itemView.findViewById(R.id.view_color_circle);
        }

        public void bind(Category category, OnCategoryClickListener listener) {
            Context context = itemView.getContext();

            // 1. Gắn Tên hạng mục
            tvName.setText(category.getCategoryName());

            // 2. Lấy màu và tô cho nền tròn phía sau
            int colorValue = AppResourceManager.getColor(category.getColor()); // Lưu ý: model của bạn có thể là getColor() hoặc getColorId()
            viewColorCircle.setBackgroundTintList(ColorStateList.valueOf(colorValue));
            viewColorCircle.setBackgroundResource(R.drawable.bg_circle);

            // 3. Lấy Icon Trắng và gắn vào ImageView
            ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, category.getIcon())); // Tương tự: getIcon() hoặc getIconId()

            // 4. Xử lý sự kiện click
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCategoryClick(category);
            });
        }
    }
}