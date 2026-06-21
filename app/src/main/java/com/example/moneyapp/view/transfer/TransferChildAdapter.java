package com.example.moneyapp.view.transfer;

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
import com.example.moneyapp.model.HistoryItem;
import com.example.moneyapp.model.Transfer;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class TransferChildAdapter extends RecyclerView.Adapter<TransferChildAdapter.ViewHolder> {
    private final List<HistoryItem> items;
    private final List<Account> accountList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transfer t);
    }

    public TransferChildAdapter(List<HistoryItem> items, List<Account> accountList, OnItemClickListener listener) {
        this.items = items;
        this.accountList = accountList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        TextView tvSourceAccount, tvDestAccount, tvNote, tvAmount;
        View divider;
        IconicsImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            tvSourceAccount = itemView.findViewById(R.id.tvSourceAccount);
            tvDestAccount = itemView.findViewById(R.id.tvDestAccount);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            divider = itemView.findViewById(R.id.divider);
            ivIcon = itemView.findViewById(R.id.iv_icon);
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
        HistoryItem item = items.get(position);

        if (item.getType() == HistoryItem.TYPE_TRANSFER) {
            // ==========================================
            // 1. THỂ HIỆN DÒNG CHUYỂN KHOẢN (TRANSFER)
            // ==========================================
            Transfer t = item.getTransfer();

            if (holder.ivIcon != null) {
                // Đổi icon swap mới
                holder.ivIcon.setIcon(new IconicsDrawable(context, "gmd-swap-horiz"));
                // ÉP CẢ Ô VIEW NHUỘM MÀU TRẮNG HỆ THỐNG ĐỂ CHỐNG MÀU ĐEN MẶC ĐỊNH
                holder.ivIcon.setColorFilter(Color.WHITE);
            }
            if (holder.flIconContainer != null) {
                holder.flIconContainer.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorInfo));
            }

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

            holder.tvAmount.setText(CurrencyFormatter.formatVND(t.getSourceAmount()) + "đ");
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorInfo));

        } else if (item.getType() == HistoryItem.TYPE_ADJUST_BALANCE) {
            // ==========================================
            // 2. THỂ HIỆN DÒNG ĐIỀU CHỈNH SỐ DƯ (ADJUST)
            // ==========================================
            var adjust = item.getAdjustBalance();

            Account acc = findAccountById(adjust.getAccountId());
            if (acc != null) {
                int actualColor = AppResourceManager.getColor(acc.getColor());
                holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(actualColor));
                // SỬ DỤNG MINH CHỨNG WHITE ICON THÀNH CÔNG CỦA BẠN
                holder.ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, acc.getIcon()));
            } else {
                holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWarning)));
                if (holder.ivIcon != null) {
                    holder.ivIcon.setIcon(new IconicsDrawable(context, "gmd-edit"));
                    holder.ivIcon.setColorFilter(Color.WHITE); // Ép trắng cho fallback
                }
            }

            holder.tvSourceAccount.setText(adjust.getAccountName());
            holder.tvSourceAccount.setVisibility(View.VISIBLE);

            holder.tvDestAccount.setText("Điều chỉnh số dư");
            holder.tvDestAccount.setTextColor(ContextCompat.getColor(context, R.color.colorOnSurfaceVariant));
            holder.tvDestAccount.setVisibility(View.VISIBLE);

            holder.tvNote.setVisibility(View.GONE);

            if (adjust.getAmount() >= 0) {
                holder.tvAmount.setText("+" + CurrencyFormatter.formatVND(adjust.getAmount()) + "đ");
                holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess));
            } else {
                holder.tvAmount.setText(CurrencyFormatter.formatVND(adjust.getAmount()) + "đ");
                holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorDanger));
            }
        }

        holder.divider.setVisibility(position == items.size() - 1 ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && item.getType() == HistoryItem.TYPE_TRANSFER) {
                listener.onItemClick(item.getTransfer());
            }
        });
    }

    private Account findAccountById(String accountId) {
        if (accountId == null || accountList == null) return null;
        for (Account a : accountList) {
            if (accountId.equals(a.getAccountId())) {
                return a;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}