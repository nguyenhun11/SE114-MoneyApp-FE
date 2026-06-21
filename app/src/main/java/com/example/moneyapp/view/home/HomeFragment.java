package com.example.moneyapp.view.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.category.CategorySummaryAdapter;
import com.example.moneyapp.view.components.TimeSelectorView;
import com.example.moneyapp.viewmodel.HomeViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategorySummaryAdapter adapter;
    private View chartsWrapper;

    // View Containers
    private View pieChartContainer;
    private View linearChartContainer;
    private AppBarLayout appBarLayout;

    // Biểu đồ & Text
    private PieChart pieChart;
    private TextView tvTotalAmountPie;
    private TextView tvTotalAmountLinear;

    private HomeViewModel homeViewModel;

    private boolean isExpenseTab = true;
    private Date currentStartDate;
    private Date currentEndDate;

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
        appBarLayout = view.findViewById(R.id.statistics);
        chartsWrapper = view.findViewById(R.id.charts_wrapper);

        pieChart = view.findViewById(R.id.main_pie_chart);
        tvTotalAmountPie = view.findViewById(R.id.tv_total_amount_pie);
        tvTotalAmountLinear = view.findViewById(R.id.tv_total_amount_linear);

        TimeSelectorView timeSelector = view.findViewById(R.id.time_selector);

        timeSelector.setOnTimeRangeChangeListener((startDate, endDate) -> {
            currentStartDate = startDate;
            currentEndDate = endDate;
            homeViewModel.setTimeRangeAndReload(startDate, endDate);
        });

        setupRecyclerView();
        setupPieChart();
        setupScrollBehavior();

        setupIncomeExpenseTabs(view, true, isExpense -> {
            isExpenseTab = isExpense;
            int tabType = isExpense ? 0 : 1;
            homeViewModel.setTabTypeAndReload(tabType);
        });
        observeViewModel();
    }

    private void observeViewModel() {
        homeViewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            String displayBalance = "0";
            if (balance != null) {
                displayBalance = String.format(Locale.getDefault(), "%,.0f đ", balance).replace(",", ".");
            }
            setupBalanceSelector(requireView(), getString(R.string.total_balance), displayBalance);
        });

        homeViewModel.getCategoryExpenses().observe(getViewLifecycleOwner(), items -> {
            adapter.updateData(items);
            populateCharts(items);
        });

        homeViewModel.getChartTotalAmount().observe(getViewLifecycleOwner(), total -> {
            if (total == null || total == 0) {
                if (tvTotalAmountPie != null) tvTotalAmountPie.setText("0đ");
                if (tvTotalAmountLinear != null) tvTotalAmountLinear.setText("0đ");
            } else {
                String formattedTotal = String.format("%,.0f", total).replace(",", ".");
                if (tvTotalAmountPie != null) tvTotalAmountPie.setText(formattedTotal);
                if (tvTotalAmountLinear != null) tvTotalAmountLinear.setText(formattedTotal);
            }
        });

        homeViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateCharts(List<PieChartItem> items) {
        if (items == null || items.isEmpty()) {
            int emptyColor = ContextCompat.getColor(requireContext(), R.color.colorEmpty);

            ArrayList<PieEntry> emptyEntries = new ArrayList<>();
            emptyEntries.add(new PieEntry(100f, ""));
            PieDataSet emptyDataSet = new PieDataSet(emptyEntries, "");
            emptyDataSet.setColor(emptyColor);
            emptyDataSet.setDrawValues(false);

            pieChart.setData(new PieData(emptyDataSet));
            pieChart.animateY(0);
            pieChart.invalidate();

            LinearLayout customLinearChart = requireView().findViewById(R.id.custom_linear_chart);
            if (customLinearChart != null) {
                customLinearChart.removeAllViews();
                View segment = new View(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                segment.setLayoutParams(params);
                segment.setBackgroundColor(emptyColor);
                customLinearChart.addView(segment);
            }
            return;
        }

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            PieChartItem item = items.get(i);
            pieEntries.add(new PieEntry(item.getPercentage(), item.getName()));
            colors.add(item.getColor());
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setDrawValues(false);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setSliceSpace(4f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.animateY(0);
        pieChart.invalidate();

        LinearLayout customLinearChart = requireView().findViewById(R.id.custom_linear_chart);
        customLinearChart.removeAllViews();

        for (int i = 0; i < items.size(); i++) {
            PieChartItem item = items.get(i);

            View segment = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    item.getPercentage()
            );

            if (i < items.size() - 1) {
                params.setMarginEnd(dpToPx(4));
            }

            segment.setLayoutParams(params);
            segment.setBackgroundColor(item.getColor());

            customLinearChart.addView(segment);
        }
    }

    private void setupScrollBehavior() {
        View topSlice = requireView().findViewById(R.id.top_slice);
        com.google.android.material.appbar.CollapsingToolbarLayout collapsingToolbar =
                requireView().findViewById(R.id.collapsing_toolbar);

        chartsWrapper.post(() -> {
            int topHeight = topSlice.getHeight();
            int linearHeight = linearChartContainer.getHeight();
            int wrapperMarginBottom = 0;
            ViewGroup.LayoutParams wrapParams = chartsWrapper.getLayoutParams();
            if (wrapParams instanceof ViewGroup.MarginLayoutParams) {
                wrapperMarginBottom = ((ViewGroup.MarginLayoutParams) wrapParams).bottomMargin;
            }

            android.widget.FrameLayout.LayoutParams params =
                    (android.widget.FrameLayout.LayoutParams) chartsWrapper.getLayoutParams();
            params.topMargin = topHeight;
            chartsWrapper.setLayoutParams(params);

            collapsingToolbar.setMinimumHeight(topHeight + linearHeight + wrapperMarginBottom);

            if (appBarLayout != null) {
                linearChartContainer.setAlpha(0f);
                linearChartContainer.setVisibility(View.INVISIBLE);
                pieChartContainer.setAlpha(1f);
                pieChartContainer.setScaleX(1f);
                pieChartContainer.setScaleY(1f);
                pieChartContainer.setVisibility(View.VISIBLE);

                appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                    int totalScrollRange = appBarLayout.getTotalScrollRange();
                    if (totalScrollRange == 0) return;

                    float percentage = (float) Math.abs(verticalOffset) / totalScrollRange;

                    float pieAlpha = Math.max(0f, 1f - (percentage * 2.5f));
                    pieChartContainer.setAlpha(pieAlpha);
                    pieChartContainer.setScaleX(0.8f + (0.2f * pieAlpha));
                    pieChartContainer.setScaleY(0.8f + (0.2f * pieAlpha));

                    float linearAlpha = Math.max(0f, Math.min(1f, (percentage - 0.5f) * 2f));
                    linearChartContainer.setAlpha(linearAlpha);
                    linearChartContainer.setTranslationY(30f * (1f - linearAlpha));

                    pieChartContainer.setVisibility(pieAlpha <= 0f ? View.INVISIBLE : View.VISIBLE);
                    linearChartContainer.setVisibility(linearAlpha > 0f ? View.VISIBLE : View.INVISIBLE);
                });
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new CategorySummaryAdapter(new ArrayList<>());
        adapter.setOnCategoryClickListener((categoryId, categoryName) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("tabType", isExpenseTab ? 1 : 2);

            bundle.putString("categoryId", categoryId);
            bundle.putString("categoryName", categoryName);

            if (currentStartDate != null) bundle.putLong("startDate", currentStartDate.getTime());
            if (currentEndDate != null) bundle.putLong("endDate", currentEndDate.getTime());

            Navigation.findNavController(requireView()).navigate(R.id.transactionFragment, bundle);
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setAdapter(adapter);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(70f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawCenterText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.animate().cancel();
        pieChart.setExtraOffsets(10f, 10f, 10f, 10f);
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}