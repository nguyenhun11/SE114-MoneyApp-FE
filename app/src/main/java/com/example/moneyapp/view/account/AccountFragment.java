package com.example.moneyapp.view.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.AccountViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class AccountFragment extends BaseFragment {

    private AccountViewModel accountViewModel;
    private AccountAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        RecyclerView rvAccounts = view.findViewById(R.id.rv_accounts);
        rvAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AccountAdapter(new ArrayList<>(), account -> {
            Toast.makeText(getContext(), "Chọn: " + account.getAccountName(), Toast.LENGTH_SHORT).show();
        });
        rvAccounts.setAdapter(adapter);

        observeViewModel(view);
    }
    @Override
    public void onResume() {
        super.onResume();
        accountViewModel.loadTotalBalance();
        accountViewModel.loadAccounts();
    }
    private void observeViewModel(View view) {
        accountViewModel.getTotalBalanceLiveData().observe(getViewLifecycleOwner(), balance -> {
            String formattedBalance = String.format(Locale.getDefault(), "%,.0f", balance).replace(",", ".");
            setupBalanceSelector(view, getString(R.string.total_balance), formattedBalance, false,
                    R.drawable.ic_transaction, v -> {
                        Toast.makeText(getContext(), "Mở Lịch sử tài khoản", Toast.LENGTH_SHORT).show();
                    },
                    R.drawable.ic_plus, v -> {
                        Toast.makeText(getContext(), "Mở Thêm tài khoản", Toast.LENGTH_SHORT).show();
                    });
        });

        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                adapter.updateList(accounts);
            }
        });

        accountViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getFabIcon() {
        return R.drawable.ic_transfer;
    }

    @Override
    protected void onFabClick() {
        Toast.makeText(getContext(), "Mở màn hình chuyển khoản", Toast.LENGTH_SHORT).show();
    }
}