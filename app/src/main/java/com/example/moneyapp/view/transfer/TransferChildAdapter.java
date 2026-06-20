package com.example.moneyapp.view.transfer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Transfer;

import java.util.List;

public class TransferChildAdapter extends RecyclerView.Adapter<TransferChildAdapter.ViewHolder> {
    private final List<Transfer> transfers;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transfer t);
    }

    public TransferChildAdapter(List<Transfer> transfers, OnItemClickListener listener) {
        this.transfers = transfers;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        TextView tvSourceAccount, tvDestAccount, tvNote, tvAmount;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            tvSourceAccount = itemView.findViewById(R.id.tvSourceAccount);
            tvDestAccount = itemView.findViewById(R.id.tvDestAccount);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            divider = itemView.findViewById(R.id.divider);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transfer_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Transfer t = transfers.get(position);

        String sourceName = t.getSourceAccountName() != null ? t.getSourceAccountName() : "N/A";
        String destName = t.getDestinationAccountName() != null ? t.getDestinationAccountName() : "N/A";

        holder.tvSourceAccount.setText("Từ: " + sourceName);
        holder.tvDestAccount.setText("Đến: " + destName);

        if (t.getDescription() != null && !t.getDescription().trim().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(t.getDescription());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        if (t.getAmount() != null) {
            holder.tvAmount.setText(t.getFormattedAmount() + "đ");
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorInfo));
        }

        if (position == transfers.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transfers != null ? transfers.size() : 0;
    }
}