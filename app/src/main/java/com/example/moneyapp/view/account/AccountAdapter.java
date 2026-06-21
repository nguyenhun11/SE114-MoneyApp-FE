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
    private final String systemCurrency; // Thêm biến lưu đơn vị mặc định
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Account account);
    }

    // Cập nhật Constructor
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

        // Lấy đơn vị tiền của cái Ví này
        String accCurrency = account.getCurrencyCode() != null ? account.getCurrencyCode() : "VND";

        // 1. Dòng chính: Số dư thực tế của ví
        String formattedBalance = CurrencyFormatter.formatVND(account.getBalance());
        holder.tvBalance.setText(String.format("%s %s", formattedBalance, accCurrency));

        // 2. Dòng phụ: Nếu ngoại tệ khác hệ thống thì quy đổi
        if (!accCurrency.equalsIgnoreCase(systemCurrency)) {
            holder.tvBaseBalance.setVisibility(View.VISIBLE);
            double rate = getMockExchangeRate(accCurrency, systemCurrency);
            double baseBalance = account.getBalance() * rate;
            String formattedBase = CurrencyFormatter.formatVND(baseBalance);
            holder.tvBaseBalance.setText(String.format("≈ %s %s", formattedBase, systemCurrency));
        } else {
            holder.tvBaseBalance.setVisibility(View.GONE);
        }

        // Xử lý ẩn/hiện tổng tài sản
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

    // Hàm mock tỷ giá
    private double getMockExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) return 1.0;
        if (fromCurrency.equals("USD") && toCurrency.equals("VND")) return 25000.0;
        if (fromCurrency.equals("EUR") && toCurrency.equals("VND")) return 27000.0;
        if (fromCurrency.equals("JPY") && toCurrency.equals("VND")) return 160.0;
        if (fromCurrency.equals("VND") && toCurrency.equals("USD")) return 1.0 / 25000.0;
        return 1.0;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        IconicsImageView ivIcon, ivHiddenEye;
        TextView tvName, tvBalance, tvBaseBalance;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            ivIcon = itemView.findViewById(R.id.iv_account_icon);
            ivHiddenEye = itemView.findViewById(R.id.iv_hidden_eye);
            tvName = itemView.findViewById(R.id.tv_account_name);
            tvBalance = itemView.findViewById(R.id.tv_account_balance);
            tvBaseBalance = itemView.findViewById(R.id.tv_account_base_balance); // Ánh xạ view mới
        }
    }
}