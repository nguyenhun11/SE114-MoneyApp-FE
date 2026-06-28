package com.example.moneyapp.view.budget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.response.BudgetResponse;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.DecimalFormat;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {
    private List<BudgetResponse> budgets;
    private final DecimalFormat formatter = new DecimalFormat("#,###");
    private final OnItemClickListener listener;
    public interface OnItemClickListener {
        void onBudgetClick(BudgetResponse budget);
    }

    public BudgetAdapter(List<BudgetResponse> budgets, OnItemClickListener listener) {
        this.budgets = budgets;
        this.listener = listener;
    }

    public void updateData(List<BudgetResponse> newBudgets) {
        this.budgets = newBudgets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BudgetResponse budget = budgets.get(position);
        Context context = holder.itemView.getContext();

        holder.tvCategoryName.setText(budget.getCategoryName());

        String periodText = "Tháng";
        if (budget.getPeriod() == 0) periodText = "Tuần";
        else if (budget.getPeriod() == 2) periodText = "Năm";

        holder.tvCycleInfo.setText(budget.getCycleName());
        holder.tvCycleInfo.setTextColor(ContextCompat.getColor(context, R.color.colorInfo));

        int progress = (int) budget.getPercentageUsed();
        holder.pbBudget.setProgress(Math.min(progress, 100)); // Không cho thanh chạy vượt khỏi viền

        String spentSummary = "Đã chi: " + formatter.format(budget.getUsedAmount()) + " / " + formatter.format(budget.getAmount()) + " đ";
        holder.tvSpentSummary.setText(spentSummary);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBudgetClick(budget);
            }
        });
        int indicatorColor;

        if (progress > 100) {
            // Vượt quá ngân sách (Đỏ)
            indicatorColor = ContextCompat.getColor(context, R.color.colorDanger);
            holder.tvPercentage.setText("Vượt mức " + progress + "%");
            holder.tvPercentage.setTextColor(indicatorColor);
            holder.tvSpentSummary.setTextColor(indicatorColor); // Cảnh báo luôn trên dòng tiền
        } else {
            // Trong mức an toàn
            holder.tvPercentage.setText(progress + "%");
            holder.tvPercentage.setTextColor(ContextCompat.getColor(context, R.color.colorOnSurfaceVariant));
            holder.tvSpentSummary.setTextColor(ContextCompat.getColor(context, R.color.colorOnSurfaceVariant));

            if (progress < 60) {
                indicatorColor = ContextCompat.getColor(context, R.color.colorSuccess);
            } else if (progress < 80) {
                indicatorColor = ContextCompat.getColor(context, R.color.colorWarning);
            } else {
                indicatorColor = ContextCompat.getColor(context, R.color.colorDanger);
            }
        }

        holder.pbBudget.setIndicatorColor(indicatorColor);
    }

    @Override
    public int getItemCount() {
        return budgets == null ? 0 : budgets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvSpentSummary, tvPercentage, tvCycleInfo;
        LinearProgressIndicator pbBudget;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvSpentSummary = itemView.findViewById(R.id.tv_spent_summary);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            tvCycleInfo = itemView.findViewById(R.id.tv_cycle_info);
            // Ánh xạ id mới
            pbBudget = itemView.findViewById(R.id.pb_budget);
        }
    }
}