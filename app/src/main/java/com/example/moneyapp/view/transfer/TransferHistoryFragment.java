package com.example.moneyapp.view.transfer;

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
import com.example.moneyapp.view.components.TimeSelectorView;
import com.example.moneyapp.viewmodel.AccountActivityViewModel;

import java.util.ArrayList;

public class TransferHistoryFragment extends BaseFragment {

    private AccountActivityViewModel viewModel;
    private AccountActivityGroupAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AccountActivityViewModel.class);

        setupHeader(view, "Lịch sử chuyển khoản", true);

        TimeSelectorView timeSelector = view.findViewById(R.id.time_selector);
        timeSelector.setOnTimeRangeChangeListener((startDate, endDate) -> {
            viewModel.setTimeRangeAndReload(startDate, endDate);
        });

        RecyclerView recyclerView = view.findViewById(R.id.rv_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AccountActivityGroupAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getGroupedActivities().observe(getViewLifecycleOwner(), groups -> {
            adapter.updateList(groups);
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected boolean shouldShowFAB() { return false; }
    
    @Override
    protected boolean shouldShowBottomNavigation() { return false; }
}
