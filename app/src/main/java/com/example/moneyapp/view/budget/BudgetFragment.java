package com.example.moneyapp.view.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.BudgetViewModel;

import java.util.ArrayList;

public class BudgetFragment extends BaseFragment {

    private BudgetViewModel viewModel;
    private RecyclerView rvBudgets;
    private BudgetAdapter adapter;
    private ProgressBar pbLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHeader(view, "Ngân sách", true);

        rvBudgets = view.findViewById(R.id.rvBudgets);
        pbLoading = view.findViewById(R.id.pbLoading);
        
        rvBudgets.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BudgetAdapter(new ArrayList<>());
        rvBudgets.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        viewModel.getBudgets().observe(getViewLifecycleOwner(), budgets -> {
            if (budgets != null) {
                adapter.updateData(budgets);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> 
            pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE)
        );

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        viewModel.fetchBudgets();
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.action_budgetFragment_to_budgetAddFragment);
    }

    @Override
    protected String getFabLabel() {
        return "Thêm ngân sách";
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

}
