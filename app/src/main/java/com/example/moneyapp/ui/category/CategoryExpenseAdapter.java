package com.example.moneyapp.ui.category;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.home.PieChartItem;

import java.text.DecimalFormat;
import java.util.List;

public class CategoryExpenseAdapter extends RecyclerView.Adapter<CategoryExpenseAdapter.ViewHolder> {

    private List<PieChartItem> items;
    private OnCategoryClickListener listener; // Bộ phát tín hiệu khi click

    // 🌟 1. Interface giao tiếp với Fragment
    public interface OnCategoryClickListener {
        void onCategoryClicked(String categoryId);
    }

    // Constructor mặc định
    public CategoryExpenseAdapter(List<PieChartItem> items) {
        this.items = items;
    }

    // Gắn "tai nghe" cho Adapter
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    // 🌟 2. Hàm update data động (Tránh việc phải tạo lại Adapter)
    public void updateData(List<PieChartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged(); // Yêu cầu RecyclerView vẽ lại
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PieChartItem item = items.get(position);

        holder.tvName.setText(item.getName());

        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvAmount.setText(formatter.format(item.getAmount()));

        holder.tvPercentage.setText(String.format("%.0f%%", item.getPercentage()));
        holder.viewColor.getBackground().setColorFilter(item.getColor(), PorterDuff.Mode.SRC_IN);

        // 🌟 3. Xử lý sự kiện bấm vào dòng
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && item.getCategoryId() != null) {
                // Bắn ID của danh mục ra ngoài cho Fragment xử lý chuyển màn hình
                listener.onCategoryClicked(item.getCategoryId());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_category_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            viewColor = itemView.findViewById(R.id.view_color);
        }
    }
}