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
        holder.tvRemaining.setText("Còn lại: " + formatter.format(budget.getRemainingAmount()) + " đ");
        holder.tvSpentSummary.setText(formatter.format(budget.getUsedAmount()) + " / " + formatter.format(budget.getAmount()));
        
        int progress = (int) budget.getPercentageUsed();
        holder.pbBudget.setProgress(Math.min(progress, 100));
        holder.tvPercentage.setText(progress + "%");

        // Color coding for progress
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
        TextView tvCategoryName, tvRemaining, tvSpentSummary, tvPercentage;
        ProgressBar pbBudget;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvRemaining = itemView.findViewById(R.id.tv_remaining);
            tvSpentSummary = itemView.findViewById(R.id.tv_spent_summary);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            pbBudget = itemView.findViewById(R.id.pb_budget);
        }
    }
}
