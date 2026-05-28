package com.example.moneyapp.ui.home;

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
import com.example.moneyapp.ui.category.CategoryExpenseAdapter;
import com.example.moneyapp.ui.BaseFragment;
import com.example.moneyapp.viewmodel.HomeViewModel;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategoryExpenseAdapter adapter;
    private View pieChartContainer;
    private View linearChartContainer;
    private AppBarLayout appBarLayout;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        rvCategories = view.findViewById(R.id.rv_categories);
        pieChartContainer = view.findViewById(R.id.pie_chart_container);
        linearChartContainer = view.findViewById(R.id.linear_chart_container);
        appBarLayout = view.findViewById(R.id.app_bar);

        setupRecyclerView();
        setupScrollBehavior();
        observeViewModel();

        homeViewModel.loadHomeData();
    }

    private void setupRecyclerView() {
        adapter = new CategoryExpenseAdapter(new ArrayList<>());
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setAdapter(adapter);
    }

    private void observeViewModel() {
        homeViewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            setupBalanceSelector(requireView(), getString(R.string.total_balance), 
                String.format("%,.0f", balance).replace(",", "."), true);
        });

        homeViewModel.getCategoryExpenses().observe(getViewLifecycleOwner(), expenses -> {
            adapter.updateData(expenses);
        });

        homeViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Show/hide progress bar if needed
        });
    }

    private void setupScrollBehavior() {
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                if (totalScrollRange == 0) return;

                float percentage = (float) Math.abs(verticalOffset) / totalScrollRange;

                float transition = (percentage - 0.1f) / (0.4f - 0.1f);
                transition = Math.max(0, Math.min(1, transition));

                pieChartContainer.setAlpha(1 - transition);
                linearChartContainer.setAlpha(transition);

                if (transition <= 0) {
                    pieChartContainer.setVisibility(View.VISIBLE);
                    linearChartContainer.setVisibility(View.GONE);
                } else if (transition >= 1) {
                    pieChartContainer.setVisibility(View.GONE);
                    linearChartContainer.setVisibility(View.VISIBLE);
                } else {
                    pieChartContainer.setVisibility(View.VISIBLE);
                    linearChartContainer.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    protected void onFabClick() {
        // Handle FAB click
    }
}
