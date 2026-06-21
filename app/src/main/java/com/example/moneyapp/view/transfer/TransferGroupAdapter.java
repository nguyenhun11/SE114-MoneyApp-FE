package com.example.moneyapp.view.transfer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.model.Account; // Import thêm model Account
import com.example.moneyapp.model.DailyTransferGroup;

import java.util.List;

public class TransferGroupAdapter extends RecyclerView.Adapter<TransferGroupAdapter.ViewHolder> {

    private List<DailyTransferGroup> groups;
    private List<Account> accountList; // Thêm biến lưu danh sách tài khoản
    private final TransferChildAdapter.OnItemClickListener childListener;

    public TransferGroupAdapter(List<DailyTransferGroup> groups, List<Account> accountList, TransferChildAdapter.OnItemClickListener listener) {
        this.groups = groups;
        this.accountList = accountList;
        this.childListener = listener;
    }

    public void updateData(List<DailyTransferGroup> newGroups, List<Account> newAccountList) {
        this.groups = newGroups;
        this.accountList = newAccountList;
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
        DailyTransferGroup group = groups.get(position);
        holder.tvDateLabel.setText(group.getDateLabel());

        TransferChildAdapter childAdapter = new TransferChildAdapter(group.getTransfers(),
                accountList,
                PreferenceManager.getInstance(holder.itemView.getContext()).getDefaultCurrency(),
                childListener);

        holder.rvDailyTransactions.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvDailyTransactions.setAdapter(childAdapter);
        holder.rvDailyTransactions.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        return groups != null ? groups.size() : 0;
    }
}