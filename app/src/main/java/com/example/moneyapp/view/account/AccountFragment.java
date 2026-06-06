package com.example.moneyapp.view.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneyapp.R;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.AccountViewModel;

import java.util.Locale;


public class AccountFragment extends BaseFragment {

    private AccountViewModel accountViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        observeViewModel(view);
        accountViewModel.loadTotalBalance();
        // Bạn có thể load thêm danh sách ví ở đây nếu Fragment có RecyclerView
    }

    private void observeViewModel(View view) {
        accountViewModel.getTotalBalanceLiveData().observe(getViewLifecycleOwner(), balance -> {
            String formattedBalance = String.format(Locale.getDefault(), "%,.0f", balance).replace(",", ".");
            setupBalanceSelector(view, getString(R.string.total_balance), formattedBalance, false);
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
