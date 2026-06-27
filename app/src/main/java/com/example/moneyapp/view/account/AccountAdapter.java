package com.example.moneyapp.view.account;

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
import com.example.moneyapp.model.Account;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> accountList;
    private final String systemCurrency;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Account account);
    }

    public AccountAdapter(List<Account> accountList, String systemCurrency, OnItemClickListener listener) {
        this.accountList = accountList;
        this.systemCurrency = systemCurrency != null ? systemCurrency : "VND";
        this.listener = listener;
    }

    public void updateList(List<Account> newList) {
        this.accountList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvName.setText(account.getAccountName());

        String accCurrency = account.getCurrencyCode() != null ? account.getCurrencyCode() : "VND";

        double availableBalance = account.getAvailableBalance();
        double lockedBalance = account.getLockedBalance();

        String formattedAvailable = CurrencyFormatter.formatVND(availableBalance);
        holder.tvAvailableBalance.setText(String.format("%s %s", formattedAvailable, accCurrency));

        if (lockedBalance > 0) {
            holder.tvLockedBalance.setVisibility(View.VISIBLE);
            String formattedLocked = CurrencyFormatter.formatVND(lockedBalance);
            holder.tvLockedBalance.setText(String.format("🔒 %s %s", formattedLocked, accCurrency));
        } else {
            holder.tvLockedBalance.setVisibility(View.GONE);
        }

        if (!accCurrency.equalsIgnoreCase(systemCurrency)) {
            holder.tvBaseBalance.setVisibility(View.VISIBLE);
            double baseBalance = CurrencyFormatter.previewConversion(availableBalance, accCurrency, systemCurrency);
            String formattedBase = CurrencyFormatter.formatVND(baseBalance);
            holder.tvBaseBalance.setText(String.format("%s %s", formattedBase, systemCurrency));
        } else {
            holder.tvBaseBalance.setVisibility(View.GONE);
        }

        if (account.isIncludeInTotal()) {
            holder.ivHiddenEye.setVisibility(View.GONE);
        } else {
            holder.ivHiddenEye.setVisibility(View.VISIBLE);
        }

        if (availableBalance < 0) {
            holder.tvAvailableBalance.setTextColor(ContextCompat.getColor(context, R.color.colorDanger));
        } else {
            int normalColor = ContextCompat.getColor(context, R.color.colorOnSurface);
            int dimColor = ContextCompat.getColor(context, R.color.colorOnSurfaceVariant);
            holder.tvAvailableBalance.setTextColor(account.isIncludeInTotal() ? normalColor : dimColor);
        }

        // 6. Hiển thị Icon và màu ví
        int actualColor = AppResourceManager.getColor(account.getColor());
        holder.ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, account.getIcon()));
        holder.flIconContainer.setBackgroundTintList(ColorStateList.valueOf(actualColor));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(account);
        });
    }

    @Override
    public int getItemCount() {
        return accountList != null ? accountList.size() : 0;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        IconicsImageView ivIcon, ivHiddenEye;
        TextView tvName, tvAvailableBalance, tvLockedBalance, tvBaseBalance;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            ivIcon = itemView.findViewById(R.id.iv_account_icon);
            ivHiddenEye = itemView.findViewById(R.id.iv_hidden_eye);
            tvName = itemView.findViewById(R.id.tv_account_name);

            tvAvailableBalance = itemView.findViewById(R.id.tv_available_balance);
            tvLockedBalance = itemView.findViewById(R.id.tv_locked_balance);
            tvBaseBalance = itemView.findViewById(R.id.tv_account_base_balance);
        }
    }
}