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
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.view.home.PieChartItem;

import java.text.DecimalFormat;
import java.util.List;

public class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder> {

    private List<PieChartItem> items;
    private OnCategoryClickListener listener;
    private final DecimalFormat formatter = new DecimalFormat("#,###");

    public interface OnCategoryClickListener {
        void onCategoryClicked(String categoryId, String categoryName);
    }

    public CategorySummaryAdapter(List<PieChartItem> items) {
        this.items = items;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<PieChartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PieChartItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.tvName.setText(item.getName());
        holder.tvAmount.setText(formatter.format(item.getAmount()));
        holder.tvPercentage.setText(String.format("%.0f%%", item.getPercentage()));
        holder.viewColor.setBackgroundTintList(ColorStateList.valueOf(item.getColor()));

        if (holder.ivIcon != null) {
            holder.ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, item.getIconId()));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && item.getCategoryId() != null) {
                listener.onCategoryClicked(item.getCategoryId(), item.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvPercentage;
        View viewColor;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_category_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            viewColor = itemView.findViewById(R.id.view_color);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
        }
    }
}