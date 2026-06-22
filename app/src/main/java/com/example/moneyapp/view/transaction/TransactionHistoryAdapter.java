package com.example.moneyapp.view.transaction;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.HistoryItem;
import com.example.moneyapp.model.Mood;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.model.Transfer;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<HistoryItem> items;
    private final List<Account> accountList; // Cần cho giao dịch chuyển khoản
    private final String systemCurrency;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    public TransactionHistoryAdapter(List<HistoryItem> items, List<Account> accountList, String systemCurrency, OnItemClickListener listener) {
        this.items = items;
        this.accountList = accountList;
        this.systemCurrency = systemCurrency != null ? systemCurrency : "VND";
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType(); // 0: Transaction, 1: Transfer, 2: AdjustBalance
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Phân nhánh giao diện XML dựa trên loại giao dịch
        if (viewType == HistoryItem.TYPE_TRANSACTION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_child, parent, false);
            return new TransactionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer_child, parent, false);
            return new TransferViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        boolean isLastItem = (position == items.size() - 1);

        if (holder instanceof TransactionViewHolder) {
            bindTransaction((TransactionViewHolder) holder, item.getTransaction(), isLastItem);
        } else if (holder instanceof TransferViewHolder) {
            bindTransferOrAdjust((TransferViewHolder) holder, item, isLastItem);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // ==========================================
    // 1. BIND DATA CHO THU / CHI
    // ==========================================
    private void bindTransaction(TransactionViewHolder holder, Transaction t, boolean isLastItem) {
        Context context = holder.itemView.getContext();

        holder.tvCategoryName.setText(t.getCategoryName() != null ? t.getCategoryName() : "Giao dịch");
        holder.tvMoodEmoji.setVisibility(View.VISIBLE);
        holder.tvMoodEmoji.setText(Mood.getEmojiById(t.getMoodId()));
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

        holder.divider.setVisibility(isLastItem ? View.GONE : View.VISIBLE);
    }

    // ==========================================
    // 2. BIND DATA CHO CHUYỂN KHOẢN / ĐIỀU CHỈNH
    // ==========================================
    private void bindTransferOrAdjust(TransferViewHolder holder, HistoryItem item, boolean isLastItem) {
        Context context = holder.itemView.getContext();

        if (item.getType() == HistoryItem.TYPE_TRANSFER) {
            Transfer t = item.getTransfer();

            holder.ivIcon.setIcon(new IconicsDrawable(context, "gmd-swap-horiz"));
            holder.ivIcon.setColorFilter(Color.WHITE);
            holder.flIconContainer.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorInfo));

            holder.tvSourceAccount.setText("Từ: " + t.getSourceAccountName());
            holder.tvSourceAccount.setVisibility(View.VISIBLE);
            holder.tvDestAccount.setText("Đến: " + t.getDestinationAccountName());
            holder.tvDestAccount.setTextColor(ContextCompat.getColor(context, R.color.colorOnSurface));
            holder.tvDestAccount.setVisibility(View.VISIBLE);

            if (t.getDescription() != null && !t.getDescription().trim().isEmpty()) {
                holder.tvNote.setVisibility(View.VISIBLE);
                holder.tvNote.setText(t.getDescription());
            } else {
                holder.tvNote.setVisibility(View.GONE);
            }

            Account srcAcc = findAccountById(t.getSourceAccountId());
            String srcCurrency = (srcAcc != null && srcAcc.getCurrencyCode() != null) ? srcAcc.getCurrencyCode() : "VND";

            holder.tvAmount.setText(CurrencyFormatter.formatVND(t.getSourceAmount()) + " " + srcCurrency);
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorInfo));

            if (!srcCurrency.equalsIgnoreCase(systemCurrency)) {
                holder.tvBaseAmount.setVisibility(View.VISIBLE);
                double baseAmt = (t.getBaseAmount() != null && t.getBaseAmount() > 0) ? t.getBaseAmount() : CurrencyFormatter.previewConversion(t.getSourceAmount(), srcCurrency, systemCurrency);
                holder.tvBaseAmount.setText("≈ " + CurrencyFormatter.formatVND(baseAmt) + " " + systemCurrency);
            } else {
                holder.tvBaseAmount.setVisibility(View.GONE);
            }

        } else if (item.getType() == HistoryItem.TYPE_ADJUST_BALANCE) {
            var adjust = item.getAdjustBalance();
            Account acc = findAccountById(adjust.getAccountId());
            String accCurrency = (acc != null && acc.getCurrencyCode() != null) ? acc.getCurrencyCode() : "VND";

            if (acc != null) {
                int actualColor = AppResourceManager.getColor(acc.getColor());
                holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(actualColor));
                holder.ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, acc.getIcon()));
            } else {
                holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWarning)));
                holder.ivIcon.setIcon(new IconicsDrawable(context, "gmd-edit"));
                holder.ivIcon.setColorFilter(Color.WHITE);
            }

            holder.tvSourceAccount.setText(adjust.getAccountName());
            holder.tvSourceAccount.setVisibility(View.VISIBLE);
            holder.tvDestAccount.setText("Điều chỉnh số dư");
            holder.tvDestAccount.setTextColor(ContextCompat.getColor(context, R.color.colorOnSurfaceVariant));
            holder.tvDestAccount.setVisibility(View.VISIBLE);
            holder.tvNote.setVisibility(View.GONE);

            String sign = adjust.getAmount() >= 0 ? "+" : "-";
            double absAmount = Math.abs(adjust.getAmount());
            int colorRes = adjust.getAmount() >= 0 ? R.color.colorSuccess : R.color.colorDanger;

            holder.tvAmount.setText(sign + " " + CurrencyFormatter.formatVND(absAmount) + " " + accCurrency);
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, colorRes));

            if (!accCurrency.equalsIgnoreCase(systemCurrency)) {
                holder.tvBaseAmount.setVisibility(View.VISIBLE);
                double baseAmt = CurrencyFormatter.previewConversion(absAmount, accCurrency, systemCurrency);
                holder.tvBaseAmount.setText("≈ " + sign + " " + CurrencyFormatter.formatVND(baseAmt) + " " + systemCurrency);
            } else {
                holder.tvBaseAmount.setVisibility(View.GONE);
            }
        }
        holder.divider.setVisibility(isLastItem ? View.GONE : View.VISIBLE);
    }

    private Account findAccountById(String accountId) {
        if (accountId == null || accountList == null) return null;
        for (Account a : accountList) {
            if (a.getAccountId() != null && accountId.equalsIgnoreCase(a.getAccountId())) return a;
        }
        return null;
    }

    // ==========================================
    // 3. CÁC VIEW HOLDER
    // ==========================================
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        IconicsImageView ivIcon;
        TextView tvCategoryName, tvAccountName, tvNote, tvAmount, tvBaseAmount, tvMoodEmoji;
        View divider;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            ivIcon = itemView.findViewById(R.id.iv_transaction_icon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvAccountName = itemView.findViewById(R.id.tvAccountName);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvBaseAmount = itemView.findViewById(R.id.tvBaseAmount);
            tvMoodEmoji = itemView.findViewById(R.id.tvMoodEmoji);
            divider = itemView.findViewById(R.id.divider);
        }
    }

    public static class TransferViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        TextView tvSourceAccount, tvDestAccount, tvNote, tvAmount, tvBaseAmount;
        View divider;
        IconicsImageView ivIcon;

        public TransferViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            tvSourceAccount = itemView.findViewById(R.id.tvSourceAccount);
            tvDestAccount = itemView.findViewById(R.id.tvDestAccount);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvBaseAmount = itemView.findViewById(R.id.tvBaseAmount);
            divider = itemView.findViewById(R.id.divider);
            ivIcon = itemView.findViewById(R.id.iv_icon);
        }
    }
}