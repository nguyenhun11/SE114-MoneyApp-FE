package com.example.moneyapp.view.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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
            Bundle args = new Bundle();
            args.putString("accountId", account.getAccountId());
            Navigation.findNavController(view).navigate(R.id.accountDetailFragment, args);
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

            setupBalanceSelector(
                    view,
                    getString(R.string.total_balance),
                    formattedBalance,
                    false,
                    "gmd_history",
                    v -> {
                        Toast.makeText(getContext(), "Lịch sử tài khoản (Đang phát triển)", Toast.LENGTH_SHORT).show();
                    },
                    "gmd_add_circle_outline",
                    v -> {
                        Navigation.findNavController(view).navigate(R.id.accountDetailFragment);
                    }
            );
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
    protected String getFabIcon() {
        return "gmd_swap_horiz";
    }

    @Override
    protected void onFabClick() {
        Toast.makeText(getContext(), "Mở màn hình chuyển khoản", Toast.LENGTH_SHORT).show();
    }
}