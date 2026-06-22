package com.example.moneyapp.view.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.DailyTransactionGroup;
import com.example.moneyapp.view.transaction.TransactionHistoryAdapter;

import java.util.List;

public class HistoryGroupAdapter extends RecyclerView.Adapter<HistoryGroupAdapter.ViewHolder> {

    private List<DailyTransactionGroup> groups;
    private List<Account> accountList; // Thêm AccountList
    private final String systemCurrency;
    private final TransactionHistoryAdapter.OnItemClickListener childListener;

    public HistoryGroupAdapter(List<DailyTransactionGroup> groups, List<Account> accountList, String systemCurrency, TransactionHistoryAdapter.OnItemClickListener listener) {
        this.groups = groups;
        this.accountList = accountList;
        this.systemCurrency = systemCurrency != null ? systemCurrency : "VND";
        this.childListener = listener;
    }

    public void updateData(List<DailyTransactionGroup> newGroups, List<Account> newAccounts) {
        this.groups = newGroups;
        this.accountList = newAccounts;
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

        // Khởi tạo Adapter con và truyền toàn bộ dữ liệu xuống
        TransactionHistoryAdapter childAdapter = new TransactionHistoryAdapter(
                group.getItems(), // Lưu ý: Đổi tên getTransactions() thành getItems() trong Model
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