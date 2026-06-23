package com.example.moneyapp.view.transfer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.AdjustBalance;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.AdjustBalanceViewModel;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdjustBalanceDetailFragment extends BaseFragment {

    private AdjustBalanceViewModel adjustBalanceViewModel;
    private AccountViewModel accountViewModel;
    private String currentAdjustId;
    private final List<Account> accountList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adjust_balance_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adjustBalanceViewModel = new ViewModelProvider(this).get(AdjustBalanceViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        Bundle args = getArguments();
        if (args != null && args.containsKey("adjustId")) {
            currentAdjustId = args.getString("adjustId");
            setupHeader(view, "Chi tiết số dư", "gmd_navigate_before", v -> Navigation.findNavController(v).navigateUp(), null, null);

            observeViewModels(view);
            accountViewModel.loadAccounts(); // Nạp thông tin Ví để lấy Icon/Màu sắc và CurrencyCode
        } else {
            Toast.makeText(getContext(), "Không tìm thấy thông tin bản ghi", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
        }
    }

    private void observeViewModels(View view) {
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);

                if (getArguments() != null && getArguments().containsKey("adjustId")) {
                    String id = getArguments().getString("adjustId");
                    String accountId = getArguments().getString("accountId");
                    String accountName = getArguments().getString("accountName");
                    double amount = getArguments().getDouble("amount", 0.0);
                    long createdAt = getArguments().getLong("createdAt", 0);

                    AdjustBalance adjust = new AdjustBalance(id, accountId, accountName, amount, new java.util.Date(createdAt));

                    adjustBalanceViewModel.setAdjustBalanceData(adjust);
                }
            }
        });

        adjustBalanceViewModel.getSelectedAdjustBalance().observe(getViewLifecycleOwner(), adjust -> {
            if (adjust == null) return;

            TextView tvAmount = view.findViewById(R.id.tvDetailAmount);
            TextView tvBaseAmountDetail = view.findViewById(R.id.tvBaseAmountDetail);
            TextView tvAccountName = view.findViewById(R.id.tvDetailAccountName);
            TextView tvCreatedAt = view.findViewById(R.id.tvCreatedAt);

            FrameLayout flAccountIcon = view.findViewById(R.id.fl_account_icon);
            IconicsImageView ivAccountIcon = view.findViewById(R.id.iv_account_icon);

            tvAccountName.setText(adjust.getAccountName() != null ? adjust.getAccountName() : "Tài khoản ẩn");

            if (adjust.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
                tvCreatedAt.setText(String.format("Thực hiện lúc: %s", sdf.format(adjust.getCreatedAt())));
            }

            Context context = requireContext();
            Account currentAccount = findAccountById(adjust.getAccountId());

            String accCurrency = (currentAccount != null && currentAccount.getCurrencyCode() != null) ? currentAccount.getCurrencyCode() : "VND";
            String systemCurrency = PreferenceManager.getInstance(context).getDefaultCurrency();

            String sign = adjust.getAmount() >= 0 ? "+" : "-";
            double absAmount = Math.abs(adjust.getAmount());
            int colorRes = adjust.getAmount() >= 0 ? R.color.colorSuccess : R.color.colorDanger;

            tvAmount.setText(String.format("%s%s %s", sign, CurrencyFormatter.formatVND(absAmount), accCurrency));
            tvAmount.setTextColor(ContextCompat.getColor(context, colorRes));

            if (!accCurrency.equalsIgnoreCase(systemCurrency)) {
                tvBaseAmountDetail.setVisibility(View.VISIBLE);
                double rate = getMockExchangeRate(accCurrency, systemCurrency);
                double baseAmount = absAmount * rate;
                tvBaseAmountDetail.setText(String.format("≈ %s%s %s", sign, CurrencyFormatter.formatVND(baseAmount), systemCurrency));
            } else {
                tvBaseAmountDetail.setVisibility(View.GONE);
            }

            if (currentAccount != null) {
                int actualColor = AppResourceManager.getColor(currentAccount.getColor());
                flAccountIcon.setBackgroundTintList(ColorStateList.valueOf(actualColor));
                ivAccountIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, currentAccount.getIcon()));
            }
        });

        adjustBalanceViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private Account findAccountById(String accountId) {
        if (accountId == null) return null;
        for (Account a : accountList) {
            if (accountId.equals(a.getAccountId())) return a;
        }
        return null;
    }

    private double getMockExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) return 1.0;
        if (fromCurrency.equals("USD") && toCurrency.equals("VND")) return 25000.0;
        if (fromCurrency.equals("EUR") && toCurrency.equals("VND")) return 27000.0;
        if (fromCurrency.equals("JPY") && toCurrency.equals("VND")) return 160.0;
        return 1.0;
    }

    @Override
    protected boolean shouldShowBottomNavigation() { return false; }
    @Override
    protected boolean shouldShowFAB() {return false; }
}