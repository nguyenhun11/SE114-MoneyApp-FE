package com.example.moneyapp.view.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.TimeSelectorView;
import com.example.moneyapp.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TransactionFragment extends BaseFragment {

    private TransactionGroupAdapter adapter;
    private TransactionViewModel transactionViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        setupThreeTabs(view, index -> {
            CategoryType type = null;
            if (index == 1) type = CategoryType.EXPENSE;
            else if (index == 2) type = CategoryType.INCOME;
            transactionViewModel.setTypeAndReload(type);
        });

        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionGroupAdapter(new ArrayList<>(), transaction -> {
            Bundle args = new Bundle();
            args.putString("transactionId", transaction.getTransactionId());
            Navigation.findNavController(view).navigate(R.id.transactionDetailFragment, args);
        });
        recyclerView.setAdapter(adapter);

        TimeSelectorView timeSelector = view.findViewById(R.id.time_selector);
        timeSelector.setOnTimeRangeChangeListener((startDate, endDate) -> {
            transactionViewModel.setTimeRangeAndReload(startDate, endDate);
        });

        setupCategoryFilter(view);
        observeViewModel();
    }

    private void observeViewModel() {
        transactionViewModel.getGroupedTransactions().observe(getViewLifecycleOwner(), items -> {
            adapter.updateList(items);
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        transactionViewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            setupBalanceSelector(requireView(), getString(R.string.total_balance),
                    String.format("%,.0f", balance).replace(",", "."), true);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        transactionViewModel.loadTotalBalance();
        transactionViewModel.reloadTransactions();
    }

    private void setupCategoryFilter(View view) {
        LinearLayout btnCategoryFilter = view.findViewById(R.id.btn_category_filter);
        btnCategoryFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mở menu chọn hạng mục", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }
}