package com.example.moneyapp.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moneyapp.R;
import com.example.moneyapp.adapter.CategoryExpenseAdapter;
import com.example.moneyapp.model.CategoryExpense;
import com.example.moneyapp.ui.BaseFragment;
import com.google.android.material.appbar.AppBarLayout;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategoryExpenseAdapter adapter;
    private View pieChartContainer;
    private View linearChartContainer;
    private AppBarLayout appBarLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategories = view.findViewById(R.id.rv_categories);
        pieChartContainer = view.findViewById(R.id.pie_chart_container);
        linearChartContainer = view.findViewById(R.id.linear_chart_container);
        appBarLayout = view.findViewById(R.id.app_bar);

        setupRecyclerView();
        setupScrollBehavior();

        // Setup header static data
        setupBalanceSelector(view, "Tổng cộng", "200.000", true);
    }

    private void setupRecyclerView() {
        List<CategoryExpense> data = new ArrayList<>();
        data.add(new CategoryExpense("Ăn uống", 30000, 50, Color.parseColor("#FFB300")));
        data.add(new CategoryExpense("Xăng", 30000, 50, Color.parseColor("#F44336")));
        data.add(new CategoryExpense("Sinh hoạt", 30000, 50, Color.parseColor("#7C4DFF")));
        data.add(new CategoryExpense("Mua sắm", 20000, 33, Color.parseColor("#4CAF50")));
        data.add(new CategoryExpense("Giải trí", 15000, 25, Color.parseColor("#2196F3")));

        adapter = new CategoryExpenseAdapter(data);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setAdapter(adapter);
    }

    private void setupScrollBehavior() {
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                if (totalScrollRange == 0) return;

                float percentage = (float) Math.abs(verticalOffset) / totalScrollRange;

                // Fast cross-fade transition between 0.1 and 0.4 of the scroll
                float transition = (percentage - 0.1f) / (0.4f - 0.1f);
                transition = Math.max(0, Math.min(1, transition));

                pieChartContainer.setAlpha(1 - transition);
                linearChartContainer.setAlpha(transition);

                // Ensure both are visible during transition to avoid "flicker" or "hidden" state
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
