package com.example.moneyapp.view.transaction;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Transaction;

import java.util.List;
import java.util.Locale;

public class TransactionChildAdapter extends RecyclerView.Adapter<TransactionChildAdapter.ViewHolder> {
    private final List<Transaction> transactions;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction t);
    }

    public TransactionChildAdapter(List<Transaction> transactions, OnItemClickListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    // Định nghĩa ViewHolder để ánh xạ View từ file XML
    public static class ViewHolder extends RecyclerView.ViewHolder {
        View viewCategoryIcon;
        TextView tvCategoryName, tvAccountName, tvNote, tvAmount;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryIcon = itemView.findViewById(R.id.viewCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvAccountName = itemView.findViewById(R.id.tvAccountName);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            divider = itemView.findViewById(R.id.divider);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp layout item_transaction_child.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Transaction t = transactions.get(position);

        holder.tvCategoryName.setText(t.getCategoryName() != null ? t.getCategoryName() : "Giao dịch");
        holder.tvAccountName.setText(t.getAccountName());

        // Xử lý ghi chú: Có thì hiện, không có thì giấu luôn để tiết kiệm diện tích
        if (t.getDescription() != null && !t.getDescription().trim().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(t.getDescription());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        // Xử lý tiền: Đổi màu theo giá trị Âm (Chi tiêu) hoặc Dương (Thu nhập)
        if (t.getAmount() != null) {
            if (t.getAmount() < 0) {
                holder.tvAmount.setText(String.format(Locale.getDefault(), "%,.0fđ", t.getAmount()).replace(",", "."));
                // Lấy màu đỏ (colorDanger) từ colors.xml
                holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorDanger));
            } else {
                holder.tvAmount.setText(String.format(Locale.getDefault(), "+%,.0fđ", t.getAmount()).replace(",", "."));
                // Lấy màu xanh (colorSuccess) từ colors.xml
                holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess));
            }
        }

        // Ẩn đường kẻ mờ ở phần tử cuối cùng của danh sách
        if (position == transactions.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        // Bắt sự kiện Click vào 1 giao dịch
        holder.itemView.setOnClickListener(v -> listener.onItemClick(t));
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }
}