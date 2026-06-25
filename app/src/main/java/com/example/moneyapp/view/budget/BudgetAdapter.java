package com.example.moneyapp.view.budget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.response.BudgetResponse;

import java.text.DecimalFormat;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {

    private List<BudgetResponse> budgets;
    private final DecimalFormat formatter = new DecimalFormat("#,###");

    public BudgetAdapter(List<BudgetResponse> budgets) {
        this.budgets = budgets;
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
        holder.tvSpentSummary.setText("Đã chi: " + formatter.format(budget.getUsedAmount()) + " / " + formatter.format(budget.getAmount()) + " đ");

        // Xử lý thông tin Chu kỳ
        String periodText = "Tháng";
        if (budget.getPeriod() == 0) periodText = "Tuần";
        else if (budget.getPeriod() == 2) periodText = "Năm";

        holder.tvCycleInfo.setText(budget.getCycleName());
        holder.tvCycleInfo.setTextColor(ContextCompat.getColor(context, R.color.colorInfo));

        int progress = (int) budget.getPercentageUsed();
        holder.pbBudget.setProgress(Math.min(progress, 100));

        if (progress > 100) {
            holder.tvPercentage.setText("Vượt mức " + progress + "%");
            holder.tvPercentage.setTextColor(ContextCompat.getColor(context, R.color.colorDanger));
        } else {
            holder.tvPercentage.setText(progress + "%");
            holder.tvPercentage.setTextColor(ContextCompat.getColor(context, R.color.colorOnSurfaceVariant));
        }

        int color;
        if (progress < 70) {
            color = ContextCompat.getColor(context, R.color.colorSuccess);
        } else if (progress < 90) {
            color = ContextCompat.getColor(context, R.color.colorWarning);
        } else {
            color = ContextCompat.getColor(context, R.color.colorDanger);
        }
        holder.pbBudget.setProgressTintList(ColorStateList.valueOf(color));
    }

    @Override
    public int getItemCount() {
        return budgets == null ? 0 : budgets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvSpentSummary, tvPercentage, tvCycleInfo;
        ProgressBar pbBudget;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvSpentSummary = itemView.findViewById(R.id.tv_spent_summary);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            tvCycleInfo = itemView.findViewById(R.id.tv_cycle_info);
            pbBudget = itemView.findViewById(R.id.pb_budget);
        }
    }
}