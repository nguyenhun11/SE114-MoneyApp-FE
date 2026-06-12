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
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;
import java.util.Locale;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> accountList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Account account);
    }

    public AccountAdapter(List<Account> accountList, OnItemClickListener listener) {
        this.accountList = accountList;
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

        holder.tvName.setText(account.getAccountName());

        String formattedBalance = String.format(Locale.getDefault(), "%,.0f", account.getBalance()).replace(",", ".");
        holder.tvBalance.setText(formattedBalance);

        Context context = holder.itemView.getContext();
        int normalColor = ContextCompat.getColor(context, R.color.colorOnSurface);
        int dimColor = ContextCompat.getColor(context, R.color.colorOnSurfaceVariant);

        if (account.isIncludeInTotal()) {
            holder.ivHiddenEye.setVisibility(View.GONE);
            holder.tvBalance.setTextColor(normalColor);
        } else {
            holder.ivHiddenEye.setVisibility(View.VISIBLE);
            holder.tvBalance.setTextColor(dimColor);
        }

        int actualColor = AppResourceManager.getColor(account.getColor());

        holder.ivIcon.setIcon(AppResourceManager.getWhiteIcon(context,account.getIcon()));
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
        TextView tvName, tvBalance;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            ivIcon = itemView.findViewById(R.id.iv_account_icon);
            ivHiddenEye = itemView.findViewById(R.id.iv_hidden_eye);
            tvName = itemView.findViewById(R.id.tv_account_name);
            tvBalance = itemView.findViewById(R.id.tv_account_balance);
        }
    }
}