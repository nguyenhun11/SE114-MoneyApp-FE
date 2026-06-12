package com.example.moneyapp.view.transaction;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.utils.AppResourceManager;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        IconicsImageView ivIcon;
        TextView tvCategoryName, tvAccountName, tvNote, tvAmount;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            ivIcon = itemView.findViewById(R.id.iv_transaction_icon);
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

        // Xử lý ghi chú
        if (t.getNote() != null && !t.getNote().trim().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(t.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        // XỬ LÝ TIỀN
        if (t.getAmount() != null) {
            CategoryType type = t.getType();
            if (type == CategoryType.EXPENSE) {
                holder.tvAmount.setText("-" + t.getFormattedAmount() + "đ");
                holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorDanger));
            } else {
                holder.tvAmount.setText("+" + t.getFormattedAmount() + "đ");
                holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess));
            }
        }

        holder.ivIcon.setIcon(AppResourceManager.getWhiteIcon(context, t.getCategoryIconId()));
        int actualColor = AppResourceManager.getColor(t.getCategoryColorId());
        holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(actualColor));

        // Ẩn đường kẻ mờ ở phần tử cuối cùng
        if (position == transactions.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(t));
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }
}
