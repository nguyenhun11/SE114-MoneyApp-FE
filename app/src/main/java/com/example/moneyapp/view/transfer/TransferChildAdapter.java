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
    private final String systemCurrency; // Thêm biến lưu hệ tiền tệ mặc định
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HistoryItem t);
    }

    // Cập nhật Constructor
    public TransferChildAdapter(List<HistoryItem> items, List<Account> accountList, String systemCurrency, OnItemClickListener listener) {
        this.items = items;
        this.accountList = accountList;
        this.systemCurrency = systemCurrency != null ? systemCurrency : "VND";
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        TextView tvSourceAccount, tvDestAccount, tvNote, tvAmount, tvBaseAmount;
        View divider;
        IconicsImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            tvSourceAccount = itemView.findViewById(R.id.tvSourceAccount);
            tvDestAccount = itemView.findViewById(R.id.tvDestAccount);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvBaseAmount = itemView.findViewById(R.id.tvBaseAmount); // Ánh xạ View mới
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
                holder.ivIcon.setIcon(new IconicsDrawable(context, "gmd-swap-horiz"));
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

            // XỬ LÝ TIỀN TỆ KÉP (TRANSFER)
            Account srcAcc = findAccountById(t.getSourceAccountId());
            String srcCurrency = (srcAcc != null && srcAcc.getCurrencyCode() != null) ? srcAcc.getCurrencyCode() : "VND";

            holder.tvAmount.setText(String.format("%s %s", CurrencyFormatter.formatVND(t.getSourceAmount()), srcCurrency));
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorInfo));

            if (!srcCurrency.equalsIgnoreCase(systemCurrency)) {
                holder.tvBaseAmount.setVisibility(View.VISIBLE);
                holder.tvBaseAmount.setText(String.format("≈ %s %s", CurrencyFormatter.formatVND(t.getBaseAmount()), systemCurrency));
            } else {
                holder.tvBaseAmount.setVisibility(View.GONE);
            }

        } else if (item.getType() == HistoryItem.TYPE_ADJUST_BALANCE) {
            var adjust = item.getAdjustBalance();

            Account acc = findAccountById(adjust.getAccountId());
            String accCurrency = "VND";

            if (acc != null) {
                accCurrency = acc.getCurrencyCode() != null ? acc.getCurrencyCode() : "VND";
                int actualColor = AppResourceManager.getColor(acc.getColor());
                holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(actualColor));
                holder.ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, acc.getIcon()));
            } else {
                holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWarning)));
                if (holder.ivIcon != null) {
                    holder.ivIcon.setIcon(new IconicsDrawable(context, "gmd-edit"));
                    holder.ivIcon.setColorFilter(Color.WHITE);
                }
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

            holder.tvAmount.setText(String.format("%s %s %s", sign, CurrencyFormatter.formatVND(absAmount), accCurrency));
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, colorRes));

            if (!accCurrency.equalsIgnoreCase(systemCurrency)) {
                holder.tvBaseAmount.setVisibility(View.VISIBLE);
                double rate = getMockExchangeRate(accCurrency, systemCurrency);
                double baseAmount = absAmount * rate;
                holder.tvBaseAmount.setText(String.format("≈ %s %s %s", sign, CurrencyFormatter.formatVND(baseAmount), systemCurrency));
            } else {
                holder.tvBaseAmount.setVisibility(View.GONE);
            }
        }

        holder.divider.setVisibility(position == items.size() - 1 ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
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

    private double getMockExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) return 1.0;
        if (fromCurrency.equals("USD") && toCurrency.equals("VND")) return 25000.0;
        if (fromCurrency.equals("EUR") && toCurrency.equals("VND")) return 27000.0;
        if (fromCurrency.equals("JPY") && toCurrency.equals("VND")) return 160.0;
        if (fromCurrency.equals("VND") && toCurrency.equals("USD")) return 1.0 / 25000.0;
        return 1.0;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}