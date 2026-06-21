package com.example.moneyapp.view.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.DailyTransactionGroup;

import java.util.List;

public class TransactionGroupAdapter extends RecyclerView.Adapter<TransactionGroupAdapter.ViewHolder> {

    private List<DailyTransactionGroup> groups;
    private final String systemCurrency; // Thêm biến lưu đơn vị tiền tệ hệ thống
    private final TransactionChildAdapter.OnItemClickListener childListener;

    // Cập nhật Constructor: Nhận thêm String systemCurrency
    public TransactionGroupAdapter(List<DailyTransactionGroup> groups, String systemCurrency, TransactionChildAdapter.OnItemClickListener listener) {
        this.groups = groups;
        this.systemCurrency = systemCurrency != null ? systemCurrency : "VND";
        this.childListener = listener;
    }

    public void updateList(List<DailyTransactionGroup> newGroups) {
        this.groups = newGroups;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateLabel;
        RecyclerView rvDailyTransactions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateLabel = itemView.findViewById(R.id.tvDateLabel);
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
        DailyTransactionGroup group = groups.get(position);
        holder.tvDateLabel.setText(group.getDateLabel());

        TransactionChildAdapter childAdapter = new TransactionChildAdapter(
                group.getTransactions(),
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