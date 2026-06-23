package com.example.moneyapp.view.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.DailyHistoryGroup; // Hoặc DailyTransactionGroup tùy bạn đặt

import java.util.List;

public class HistoryGroupAdapter extends RecyclerView.Adapter<HistoryGroupAdapter.ViewHolder> {

    private List<DailyHistoryGroup> groups;
    private List<Account> accountList;
    private final String systemCurrency;
    private final HistoryItemAdapter.OnItemClickListener childListener;

    public HistoryGroupAdapter(List<DailyHistoryGroup> groups, List<Account> accountList, String systemCurrency, HistoryItemAdapter.OnItemClickListener listener) {
        this.groups = groups;
        this.accountList = accountList;
        this.systemCurrency = systemCurrency != null ? systemCurrency : "VND";
        this.childListener = listener;
    }

    public void updateData(List<DailyHistoryGroup> newGroups, List<Account> newAccounts) {
        this.groups = newGroups;
        this.accountList = newAccounts;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateLabel;
        TextView tvDateSummary;
        RecyclerView rvDailyTransactions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateLabel = itemView.findViewById(R.id.tvDateLabel);
            tvDateSummary = itemView.findViewById(R.id.tvDateSummary);
            rvDailyTransactions = itemView.findViewById(R.id.rvDailyTransactions);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyHistoryGroup group = groups.get(position);

        holder.tvDateLabel.setText(group.getDateLabel());
        if (holder.tvDateSummary != null) {
            String rawSummary = group.getDateSummary();
            holder.tvDateSummary.setText(rawSummary + " " + systemCurrency);
        }

        HistoryItemAdapter childAdapter = new HistoryItemAdapter(
                group.getItems(),
                accountList,
                systemCurrency,
                childListener
        );

        holder.rvDailyTransactions.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvDailyTransactions.setAdapter(childAdapter);
        holder.rvDailyTransactions.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        return groups != null ? groups.size() : 0;
    }
}