package com.example.moneyapp.view.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.mikepenz.iconics.view.IconicsImageView;

public class AccountSelectorView extends LinearLayout {

    private LinearLayout container;
    private FrameLayout flIconBg;
    private IconicsImageView ivIcon;
    private TextView tvName;
    private TextView tvBalance;

    private Account currentAccount;

    public AccountSelectorView(Context context) {
        super(context);
        init(context);
    }

    public AccountSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_account_selector, this, true);

        container = findViewById(R.id.container);
        flIconBg = findViewById(R.id.fl_icon_bg);
        ivIcon = findViewById(R.id.iv_icon);
        tvName = findViewById(R.id.tv_name);
        tvBalance = findViewById(R.id.tv_balance);
    }

    // --- CÁC HÀM API ĐỂ DÙNG BÊN NGOÀI ---

    public void setAccount(Account account, boolean showBalance) {
        this.currentAccount = account;

        if (account == null) {
            clear("Chọn tài khoản...");
            return;
        }

        flIconBg.setVisibility(View.VISIBLE);
        tvName.setText(account.getAccountName());

        int actualColor = AppResourceManager.getColor(account.getColor());
        flIconBg.setBackgroundTintList(ColorStateList.valueOf(actualColor));
        ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(getContext(), account.getIcon()));

        if (showBalance && account.getTotalBalance() != null) {
            tvBalance.setVisibility(View.VISIBLE);
            tvBalance.setText("Số dư: " + CurrencyFormatter.formatVND(account.getTotalBalance()) + "đ");
        } else {
            tvBalance.setVisibility(View.GONE);
        }
    }

    public void setPreloadedData(String name, Integer iconId, Integer colorId) {
        flIconBg.setVisibility(View.VISIBLE);
        tvName.setText(name != null ? name : "N/A");

        // Load màu sắc
        if (colorId != null) {
            int actualColor = AppResourceManager.getColor(colorId);
            flIconBg.setBackgroundTintList(ColorStateList.valueOf(actualColor));
        }

        // Load icon
        if (iconId != null) {
            ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(getContext(), iconId));
        } else {
            ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(getContext(), 0));
        }

        tvBalance.setVisibility(View.GONE); // Số dư sẽ update sau nếu cần
    }

    public void clear(String hintText) {
        this.currentAccount = null;
        flIconBg.setVisibility(View.GONE); // Ẩn icon
        tvBalance.setVisibility(View.GONE); // Ẩn số dư
        tvName.setText(hintText);
    }

    public Account getSelectedAccount() {
        return currentAccount;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        container.setOnClickListener(l);
    }
}