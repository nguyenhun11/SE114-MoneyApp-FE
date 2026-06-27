package com.example.moneyapp.view.history;

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
import com.google.android.material.card.MaterialCardView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class HistoryItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<HistoryItem> items;
    private final List<Account> accountList;
    private final String systemCurrency;
    private boolean isShowTypeTag = false;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    public HistoryItemAdapter(List<HistoryItem> items, List<Account> accountList, String systemCurrency, OnItemClickListener listener) {
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
            bindTransferAdjustOrGoal((TransferViewHolder) holder, item, isLastItem);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

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
            holder.tvBaseAmount.setText(sign + " " + baseAmountFormatted + " " + systemCurrency);
        } else {
            holder.tvBaseAmount.setVisibility(View.GONE);
        }

        holder.ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, t.getCategoryIconId()));
        int actualColor = AppResourceManager.getColor(t.getCategoryColorId());
        holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(actualColor));

        holder.divider.setVisibility(isLastItem ? View.GONE : View.VISIBLE);
        setupTypeTag(holder, HistoryItem.TYPE_TRANSACTION, t.getType(), false);
    }

    private void bindTransferAdjustOrGoal(TransferViewHolder holder, HistoryItem item, boolean isLastItem) {
        Context context = holder.itemView.getContext();

        if (item.getType() == HistoryItem.TYPE_TRANSFER) {
            Transfer t = item.getTransfer();

            holder.ivIcon.setIcon(new IconicsDrawable(context, "gmd-swap-horiz"));
            holder.ivIcon.setColorFilter(Color.WHITE);
            holder.flIconContainer.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorPrimary));

            holder.tvSourceAccount.setText("Chuyển từ: " + t.getSourceAccountName());
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
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

            if (!srcCurrency.equalsIgnoreCase(systemCurrency)) {
                holder.tvBaseAmount.setVisibility(View.VISIBLE);
                double baseAmt = (t.getBaseAmount() != null && t.getBaseAmount() > 0) ? t.getBaseAmount() : CurrencyFormatter.previewConversion(t.getSourceAmount(), srcCurrency, systemCurrency);
                holder.tvBaseAmount.setText(CurrencyFormatter.formatVND(baseAmt) + " " + systemCurrency);
            } else {
                holder.tvBaseAmount.setVisibility(View.GONE);
            }

        }
        else if (item.getType() == HistoryItem.TYPE_ADJUST_BALANCE) {
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
                holder.tvBaseAmount.setText(sign + " " + CurrencyFormatter.formatVND(baseAmt) + " " + systemCurrency);
            } else {
                holder.tvBaseAmount.setVisibility(View.GONE);
            }
        }
        else if (item.getType() == HistoryItem.TYPE_GOAL_RECORD) {
            var record = item.getGoalRecord();
            boolean isDeposit = "Deposit".equalsIgnoreCase(record.getType());
            String accName = record.getAccountName() != null ? record.getAccountName() : "Ví";

            holder.ivIcon.setIcon(new IconicsDrawable(context, isDeposit ? "gmd-file-download" : "gmd-file-upload"));
            holder.ivIcon.setColorFilter(Color.WHITE);
            holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, isDeposit ? R.color.colorInfo : R.color.colorWarning)));

            String goalName = (record.getGoalName() != null && !record.getGoalName().isEmpty())
                    ? record.getGoalName()
                    : "Mục tiêu tiết kiệm";

            if (isDeposit) {
                holder.tvSourceAccount.setText("Nạp từ: " + accName);
                holder.tvDestAccount.setText("Vào: " + goalName);
            } else {
                holder.tvSourceAccount.setText("Rút từ: " + goalName);
                holder.tvDestAccount.setText("Về: " + accName);
            }
            holder.tvSourceAccount.setVisibility(View.VISIBLE);
            holder.tvDestAccount.setVisibility(View.VISIBLE);
            holder.tvDestAccount.setTextColor(ContextCompat.getColor(context, R.color.colorOnSurfaceVariant));
            holder.tvNote.setVisibility(View.GONE);

            // 3. Tiền tệ
            String sign = isDeposit ? "+" : "-";
            int colorRes = isDeposit ? R.color.colorInfo : R.color.colorWarning;
            holder.tvAmount.setText(sign + " " + CurrencyFormatter.formatVND(record.getAmount()) + " " + systemCurrency);
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, colorRes));
            holder.tvBaseAmount.setVisibility(View.GONE);
        }
        holder.divider.setVisibility(isLastItem ? View.GONE : View.VISIBLE);

        boolean isDeposit = item.getType() == HistoryItem.TYPE_GOAL_RECORD && "Deposit".equalsIgnoreCase(item.getGoalRecord().getType());
        setupTypeTag(holder, item.getType(), null, isDeposit);
    }

    private void setupTypeTag(RecyclerView.ViewHolder holder, int itemType, CategoryType transactionType, boolean isDeposit) {
        MaterialCardView cardTag = null;
        TextView tvTag = null;
        Context context = holder.itemView.getContext();

        if (holder instanceof TransactionViewHolder) {
            cardTag = ((TransactionViewHolder) holder).cardTypeTag;
            tvTag = ((TransactionViewHolder) holder).tvTypeTag;
        } else if (holder instanceof TransferViewHolder) {
            cardTag = ((TransferViewHolder) holder).cardTypeTag;
            tvTag = ((TransferViewHolder) holder).tvTypeTag;
        }

        if (cardTag == null || tvTag == null) return;

        if (!isShowTypeTag) {
            cardTag.setVisibility(View.GONE);
            return;
        }

        cardTag.setVisibility(View.VISIBLE);
        int colorRes = R.color.colorOnSurface;
        int bgLightRes = R.color.colorSurface;
        String tagText = "";

        if (itemType == HistoryItem.TYPE_TRANSACTION) {
            if (transactionType == CategoryType.EXPENSE) {
                colorRes = R.color.colorDanger;
                bgLightRes = R.color.colorDangerBgLight;
                tagText = "Chi tiêu";
            } else {
                colorRes = R.color.colorSuccess;
                bgLightRes = R.color.colorSuccessBgLight;
                tagText = "Thu nhập";
            }
        } else if (itemType == HistoryItem.TYPE_TRANSFER) {
            colorRes = R.color.colorPrimary;
            bgLightRes = R.color.colorPrimaryBgLight;
            tagText = "Chuyển khoản";
        } else if (itemType == HistoryItem.TYPE_ADJUST_BALANCE) {
            colorRes = R.color.colorNeutral;
            bgLightRes = R.color.colorNeutralBgLight;
            tagText = "Điều chỉnh số dư";
        } else if (itemType == HistoryItem.TYPE_GOAL_RECORD) {
            colorRes = isDeposit ? R.color.colorInfo : R.color.colorWarning;
            bgLightRes = isDeposit ? R.color.colorInfoBgLight : R.color.colorWarningBgLight;
            tagText = isDeposit ? "Nạp tiết kiệm" : "Rút tiết kiệm";
        }

        tvTag.setText(tagText);
        tvTag.setTextColor(ContextCompat.getColor(context, colorRes));
        cardTag.setStrokeColor(ContextCompat.getColor(context, colorRes));
        cardTag.setCardBackgroundColor(ContextCompat.getColor(context, bgLightRes));
    }
    private Account findAccountById(String accountId) {
        if (accountId == null || accountList == null) return null;
        for (Account a : accountList) {
            if (a.getAccountId() != null && accountId.equalsIgnoreCase(a.getAccountId())) return a;
        }
        return null;
    }

    public void setShowTypeTag(boolean show) {
        this.isShowTypeTag = show;
        notifyDataSetChanged();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        IconicsImageView ivIcon;
        MaterialCardView cardTypeTag;
        TextView tvTypeTag, tvCategoryName, tvAccountName, tvNote, tvAmount, tvBaseAmount, tvMoodEmoji;
        View divider;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            ivIcon = itemView.findViewById(R.id.iv_transaction_icon);
            cardTypeTag = itemView.findViewById(R.id.card_type_tag);
            tvTypeTag = itemView.findViewById(R.id.tv_type_tag);
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
        MaterialCardView cardTypeTag;
        TextView tvTypeTag, tvSourceAccount, tvDestAccount, tvNote, tvAmount, tvBaseAmount;
        View divider;
        IconicsImageView ivIcon;

        public TransferViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            cardTypeTag = itemView.findViewById(R.id.card_type_tag);
            tvTypeTag = itemView.findViewById(R.id.tv_type_tag);
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