package com.example.moneyapp.view.transaction;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.utils.ResourceMapper;

import java.util.List;

public class CategoryQuickAdapter extends RecyclerView.Adapter<CategoryQuickAdapter.ViewHolder> {
    private List<Category> list;
    private int selectedPosition = -1; // -1 là chưa chọn cái nào
    private OnCategoryClickListener listener;

    public CategoryQuickAdapter(List<Category> list, OnCategoryClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = list.get(position);
        holder.tvName.setText(category.getCategoryName());

        holder.ivIcon.setImageResource(ResourceMapper.getIconResourceById(category.getIcon()));
        int colorRes = ResourceMapper.getColorResourceById(category.getColor());
        holder.viewColorCircle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), colorRes)));

        if (selectedPosition == position) {
            holder.cardBg.setStrokeWidth(3);
            holder.cardBg.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
        } else {
            holder.cardBg.setStrokeWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);

            // Bắn sự kiện ra ngoài cho Fragment biết
            if (listener != null) {
                listener.onCategoryClick(list.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public Category getSelectedCategory() {
        if (selectedPosition != -1 && selectedPosition < list.size()) return list.get(selectedPosition);
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivIcon;
        View viewColorCircle;
        com.google.android.material.card.MaterialCardView cardBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_category_name);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            viewColorCircle = itemView.findViewById(R.id.view_color_circle);
            cardBg = itemView.findViewById(R.id.card_item_bg);
        }
    }
}