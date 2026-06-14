package com.example.moneyapp.view.category;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.utils.AppResourceManager;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.Collections;
import java.util.List;

public class ReorderCategoryAdapter extends RecyclerView.Adapter<ReorderCategoryAdapter.ViewHolder> {

    private final List<Category> categories;

    public ReorderCategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reorder_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final View viewColorCircle;
        private final IconicsImageView ivIcon;
        private final TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColorCircle = itemView.findViewById(R.id.view_color_circle);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
        }

        public void bind(Category category) {
            tvName.setText(category.getCategoryName());
            
            int colorValue = AppResourceManager.getColor(category.getColor());
            viewColorCircle.setBackgroundTintList(ColorStateList.valueOf(colorValue));
            
            String iconName = AppResourceManager.getIconName(category.getIcon());
            Context context = itemView.getContext();
            ivIcon.setIcon(new IconicsDrawable(context, iconName));
        }
    }
}
