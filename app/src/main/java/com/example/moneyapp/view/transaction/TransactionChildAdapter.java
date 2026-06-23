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
import com.example.moneyapp.model.Mood;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class TransactionChildAdapter extends RecyclerView.Adapter<TransactionChildAdapter.ViewHolder> {
    private final List<Transaction> transactions;
    private final String systemCurrency;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction t);
    }

    public TransactionChildAdapter(List<Transaction> transactions, String systemCurrency, OnItemClickListener listener) {
        this.transactions = transactions;
        this.systemCurrency = systemCurrency != null ? systemCurrency : "VND";
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        IconicsImageView ivIcon;
        TextView tvCategoryName, tvAccountName, tvNote, tvAmount, tvBaseAmount, tvMoodEmoji;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            ivIcon = itemView.findViewById(R.id.iv_transaction_icon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvAccountName = itemView.findViewById(R.id.tvAccountName);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvBaseAmount = itemView.findViewById(R.id.tvBaseAmount); // Ánh xạ view mới
            tvMoodEmoji = itemView.findViewById(R.id.tvMoodEmoji);
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

        // Thêm Emoji tâm trạng (Chỉ hiện cho khoản Chi tiêu)
        if (t.getType() == CategoryType.EXPENSE) {
            holder.tvMoodEmoji.setVisibility(View.VISIBLE);
            holder.tvMoodEmoji.setText(Mood.getEmojiById(t.getMoodId()));
        } else {
            holder.tvMoodEmoji.setVisibility(View.GONE);
        }

        holder.tvAccountName.setText(t.getAccountName());

        if (t.getNote() != null && !t.getNote().trim().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(t.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        String transactionCurrency = t.getCurrencyCode() != null ? t.getCurrencyCode() : "VND";
        String sign = (t.getType() == CategoryType.EXPENSE) ? "-" : "+";
        int colorRes = (t.getType() == CategoryType.EXPENSE) ? R.color.colorDanger : R.color.colorSuccess;

        String mainAmountFormatted = CurrencyFormatter.formatVND(t.getOriginalAmount());
        holder.tvAmount.setText(sign + " " + mainAmountFormatted + " " + transactionCurrency);
        holder.tvAmount.setTextColor(ContextCompat.getColor(context, colorRes));

        if (!transactionCurrency.equalsIgnoreCase(systemCurrency)) {
            holder.tvBaseAmount.setVisibility(View.VISIBLE);
            String baseAmountFormatted = CurrencyFormatter.formatVND(t.getBaseAmount());
            holder.tvBaseAmount.setText("≈ " + sign + " " + baseAmountFormatted + " " + systemCurrency);
        } else {
            holder.tvBaseAmount.setVisibility(View.GONE);
        }
        holder.ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, t.getCategoryIconId()));
        int actualColor = AppResourceManager.getColor(t.getCategoryColorId());
        holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(actualColor));

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