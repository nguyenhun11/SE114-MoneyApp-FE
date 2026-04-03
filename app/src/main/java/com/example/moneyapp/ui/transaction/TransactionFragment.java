package com.example.moneyapp.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;

public class TransactionFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBalanceSelector(view, "Tổng cộng", "2.500.000", true);
        setupIncomeExpenseTabs(view, isExpense -> {
        });

        view.findViewById(R.id.tv_goto_detail).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.transactionDetailFragment);
        });
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}
