package com.example.moneyapp.view.transfer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.DailyTransferGroup;

import java.util.List;

public class TransferGroupAdapter extends RecyclerView.Adapter<TransferGroupAdapter.ViewHolder> {

    private List<DailyTransferGroup> groups;
    private final TransferChildAdapter.OnItemClickListener childListener;

    public TransferGroupAdapter(List<DailyTransferGroup> groups, TransferChildAdapter.OnItemClickListener listener) {
        this.groups = groups;
        this.childListener = listener;
    }

    public void updateList(List<DailyTransferGroup> newGroups) {
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
        DailyTransferGroup group = groups.get(position);
        holder.tvDateLabel.setText(group.getDateLabel());
        TransferChildAdapter childAdapter = new TransferChildAdapter(group.getTransfers(), childListener);

        holder.rvDailyTransactions.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvDailyTransactions.setAdapter(childAdapter);
        holder.rvDailyTransactions.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        return groups != null ? groups.size() : 0;
    }
}
