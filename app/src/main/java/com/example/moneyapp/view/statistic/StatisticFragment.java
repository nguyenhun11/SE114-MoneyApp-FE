package com.example.moneyapp.view.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.response.CashFlowBarDto;
import com.example.moneyapp.data.remote.response.CategoryPieChartDto;
import com.example.moneyapp.data.remote.response.StackedBarChartDto;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.category.CategorySummaryAdapter;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.view.components.CustomMarkerView;
import com.example.moneyapp.view.components.StatisticTimeSelectorView;
import com.example.moneyapp.view.home.PieChartItem;
import com.example.moneyapp.viewmodel.StatisticViewModel;
import com.example.moneyapp.viewmodel.TransactionViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticFragment extends BaseFragment {

    private StatisticViewModel statisticViewModel;
    private TransactionViewModel transactionViewModel;
    private int currentTab = 0; // 0: Chung, 1: Chi, 2: Thu, 3: Tâm trạng

    private Date currentStartDate;
    private Date currentEndDate;

    private BarChart barChart;
    private com.github.mikephil.charting.charts.PieChart pieChartMood;
    private StatisticTimeSelectorView timeSelector;
    private int currentGroupBy = 2; // Mặc định là 2 (Tháng)
    private RecyclerView rvStatisticDetails;
    private CategorySummaryAdapter adapter;

    // Bộ nhớ đệm lưu trữ dữ liệu từ API
    private List<CashFlowBarDto> currentCashFlowData = new ArrayList<>();
    private List<StackedBarChartDto> currentExpenseStackedData = new ArrayList<>();
    private List<StackedBarChartDto> currentIncomeStackedData = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statisticViewModel = new ViewModelProvider(this).get(StatisticViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        setupHeader(view, R.string.stats_screen_title, false);

        barChart = view.findViewById(R.id.barChartCashFlow);
        pieChartMood = view.findViewById(R.id.pieChartMood);
        timeSelector = view.findViewById(R.id.time_selector_stat);
        rvStatisticDetails = view.findViewById(R.id.rv_statistic_details);

        setupRecyclerView();
        setupBarChartStyle();
        setupPieChartStyle();

        setupFourTabs(view, 0, index -> {
            currentTab = index;
            loadDataByTab();
        });

        timeSelector.setOnTimeRangeChangeListener((startDate, endDate, groupBy) -> {
            currentStartDate = startDate;
            currentEndDate = endDate;
            currentGroupBy = groupBy;
            loadDataByTab();
        });

        observeViewModel();
    }

    private void setupRecyclerView() {
        rvStatisticDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategorySummaryAdapter(new ArrayList<>());
        rvStatisticDetails.setAdapter(adapter);
    }

    private void setupBarChartStyle() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setHighlightFullBarEnabled(false);
        barChart.setDrawValueAboveBar(true);

        barChart.setExtraBottomOffset(15f);
        barChart.setExtraTopOffset(25f);

        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        // ĐÃ XÓA xAxis.setCenterAxisLabels(true) Ở ĐÂY - Sẽ set động trong từng hàm render

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);

        CustomMarkerView mv = new CustomMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(barChart);
        barChart.setMarker(mv);

        // LẮNG NGHE SỰ KIỆN CLICK CỘT
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                updateListFromChartSelection(index, h);
            }

            @Override
            public void onNothingSelected() {
                adapter.updateData(new ArrayList<>());
            }
        });

        barChart.animateY(1000);
    }

    private void setupPieChartStyle() {
        pieChartMood.setUsePercentValues(true);
        pieChartMood.getDescription().setEnabled(false);
        pieChartMood.setExtraOffsets(5, 10, 5, 5);
        pieChartMood.setDragDecelerationFrictionCoef(0.95f);
        pieChartMood.setDrawHoleEnabled(true);
        pieChartMood.setHoleColor(android.R.color.transparent);
        pieChartMood.setTransparentCircleRadius(61f);
        pieChartMood.setHoleRadius(58f);
        pieChartMood.setDrawCenterText(true);
        pieChartMood.setRotationAngle(0);
        pieChartMood.setRotationEnabled(true);
        pieChartMood.setHighlightPerTapEnabled(true);
        pieChartMood.getLegend().setEnabled(false);
    }

    private void updateListFromChartSelection(int index, Highlight h) {
        List<PieChartItem> list = new ArrayList<>();

        if (currentTab == 0) {
            // TAB CHUNG
            if (index < 0 || index >= currentCashFlowData.size()) return;
            String period = currentCashFlowData.get(index).getPeriod();
            int clickedColumn = h.getDataSetIndex(); // 0: Thu, 1: Chi, 2: Cân bằng

            if (clickedColumn == 0) {
                StackedBarChartDto periodData = findPeriodData(currentIncomeStackedData, period);
                if (periodData != null) list = convertToPieChartItems(periodData.getCategoryBreakdowns());
            } else if (clickedColumn == 1) {
                StackedBarChartDto periodData = findPeriodData(currentExpenseStackedData, period);
                if (periodData != null) list = convertToPieChartItems(periodData.getCategoryBreakdowns());
            } else {
                list = new ArrayList<>(); // Click cột Cân bằng -> Không hiện gì cả
            }
        } else {
            // TAB CHI HOẶC THU
            List<StackedBarChartDto> targetData = (currentTab == 1) ? currentExpenseStackedData : currentIncomeStackedData;

            if (index < 0 || index >= targetData.size()) return;
            StackedBarChartDto periodData = targetData.get(index);
            list = convertToPieChartItems(periodData.getCategoryBreakdowns());
        }

        adapter.updateData(list);
    }

    private StackedBarChartDto findPeriodData(List<StackedBarChartDto> list, String period) {
        if (list == null) return null;
        for (StackedBarChartDto dto : list) {
            if (dto.getPeriod() != null && dto.getPeriod().equals(period)) return dto;
        }
        return null;
    }

    private List<PieChartItem> convertToPieChartItems(List<CategoryPieChartDto> breakdowns) {
        List<PieChartItem> list = new ArrayList<>();
        if (breakdowns == null || breakdowns.isEmpty()) return list;

        double total = 0;
        for (CategoryPieChartDto cat : breakdowns) total += cat.getTotalAmount();

        for (CategoryPieChartDto cat : breakdowns) {
            int actualColor = AppResourceManager.getColor(cat.getColorId());
            float percent = (total > 0) ? (float) (cat.getTotalAmount() / total * 100) : 0f;

            list.add(new PieChartItem(
                    cat.getCategoryId(),
                    cat.getCategoryName(),
                    cat.getTotalAmount(),
                    percent,
                    actualColor,
                    cat.getIconId()));
        }
        list.sort((o1, o2) -> Double.compare(o2.getAmount(), o1.getAmount()));
        return list;
    }

    private void observeViewModel() {
        statisticViewModel.getCashFlowData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                currentCashFlowData = data;
                if (currentTab == 0) renderGroupedBarChart(data);
            }
        });

        statisticViewModel.getExpenseStackedBarData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                currentExpenseStackedData = data;
                if (currentTab == 1) {
                    renderStackedBarChart(data);
                }
            }
        });

        statisticViewModel.getIncomeStackedBarData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                currentIncomeStackedData = data;
                if (currentTab == 2) {
                    renderStackedBarChart(data);
                }
            }
        });

        statisticViewModel.getMoodSpendingData().observe(getViewLifecycleOwner(), data -> {
            if (currentTab == 3) {
                renderMoodPieChart(data);
                adapter.updateData(data);
            }
        });
        
        transactionViewModel.getGroupedTransactions().observe(getViewLifecycleOwner(), groups -> {
            if (currentTab == 3) {
                List<Transaction> allTransactions = new ArrayList<>();
                if (groups != null) {
                    for (com.example.moneyapp.model.DailyTransactionGroup g : groups) {
                        allTransactions.addAll(g.getTransactions());
                    }
                }
                statisticViewModel.calculateMoodSpending(allTransactions);
            }
        });

        statisticViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataByTab() {
        if (currentEndDate == null) return;
        adapter.updateData(new ArrayList<>());
        
        // Reset Visibility mặc định
        barChart.setVisibility(View.VISIBLE);
        pieChartMood.setVisibility(View.GONE);
        timeSelector.setVisibility(View.VISIBLE);

        switch (currentTab) {
            case 0:
                statisticViewModel.loadCashFlow(currentStartDate, currentEndDate, currentGroupBy);
                statisticViewModel.loadExpenseStackedBar(currentStartDate, currentEndDate, currentGroupBy);
                statisticViewModel.loadIncomeStackedBar(currentStartDate, currentEndDate, currentGroupBy);
                break;
            case 1:
                statisticViewModel.loadExpenseStackedBar(currentStartDate, currentEndDate, currentGroupBy);
                break;
            case 2:
                statisticViewModel.loadIncomeStackedBar(currentStartDate, currentEndDate, currentGroupBy);
                break;
            case 3:
                barChart.setVisibility(View.GONE);
                pieChartMood.setVisibility(View.VISIBLE);
                transactionViewModel.loadTransactions(currentStartDate, currentEndDate, null, null, null);
                break;
        }
    }

    private void renderMoodPieChart(List<PieChartItem> data) {
        if (data == null || data.isEmpty()) {
            pieChartMood.clear();
            return;
        }

        ArrayList<com.github.mikephil.charting.data.PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (PieChartItem item : data) {
            entries.add(new com.github.mikephil.charting.data.PieEntry(item.getPercentage(), item.getName()));
            colors.add(item.getColor());
        }

        com.github.mikephil.charting.data.PieDataSet dataSet = new com.github.mikephil.charting.data.PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setDrawValues(false);

        com.github.mikephil.charting.data.PieData pieData = new com.github.mikephil.charting.data.PieData(dataSet);
        pieChartMood.setData(pieData);
        pieChartMood.setCenterText("Chi tiêu\ntâm trạng");
        pieChartMood.animateY(1000);
        pieChartMood.invalidate();
    }

    private void renderGroupedBarChart(List<CashFlowBarDto> data) {
        if (data == null || data.isEmpty()) {
            barChart.clear();
            return;
        }

        barChart.highlightValues(null);
        barChart.fitScreen();

        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();
        ArrayList<BarEntry> balanceEntries = new ArrayList<>();
        ArrayList<Integer> balanceColors = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();

        int colorSuccess = ContextCompat.getColor(requireContext(), R.color.colorSuccess);
        int colorDanger = ContextCompat.getColor(requireContext(), R.color.colorDanger);
        int colorInfor = ContextCompat.getColor(requireContext(), R.color.colorInfo);
        int colorWarning = ContextCompat.getColor(requireContext(), R.color.colorWarning);

        for (int i = 0; i < data.size(); i++) {
            CashFlowBarDto item = data.get(i);
            incomeEntries.add(new BarEntry(i, (float) item.getTotalIncome(), item.getTotalIncome()));
            expenseEntries.add(new BarEntry(i, (float) item.getTotalExpense(), item.getTotalExpense()));
            float absoluteBalance = (float) Math.abs(item.getNetBalance());
            balanceEntries.add(new BarEntry(i, absoluteBalance, item.getNetBalance()));

            if (item.getNetBalance() >= 0) balanceColors.add(colorSuccess);
            else balanceColors.add(colorDanger);

            xLabels.add(item.getPeriod());
        }

        BarDataSet setIncome = new BarDataSet(incomeEntries, "Thu nhập");
        setIncome.setColor(colorInfor);
        setIncome.setHighlightEnabled(true);

        BarDataSet setExpense = new BarDataSet(expenseEntries, "Chi tiêu");
        setExpense.setColor(colorWarning);
        setExpense.setHighlightEnabled(true);

        BarDataSet setBalance = new BarDataSet(balanceEntries, "Cân bằng");
        setBalance.setColors(balanceColors);
        setBalance.setHighlightEnabled(true);

        BarData barData = new BarData(setIncome, setExpense, setBalance);
        barData.setDrawValues(false);

        // TRẢ LẠI TỶ LỆ CHUẨN: (0.2 + 0.05) * 3 + 0.25 = 1.0f
        float groupSpace = 0.25f;
        float barSpace = 0.05f;
        float barWidth = 0.2f;

        barChart.setData(barData);
        barChart.getBarData().setBarWidth(barWidth);

        float groupWidth = barChart.getBarData().getGroupWidth(groupSpace, barSpace);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));

        // FIX LỆCH NHÃN: Ép CenterAxisLabels bật cho biểu đồ cụm
        barChart.getXAxis().setCenterAxisLabels(true);
        barChart.getXAxis().setAxisMinimum(0f);

        int visibleGroups = 5;
        float maxX = Math.max(data.size(), visibleGroups) * groupWidth;
        barChart.getXAxis().setAxisMaximum(maxX);

        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setXEntrySpace(20f);
        legend.setYEntrySpace(5f);

        LegendEntry l1 = new LegendEntry("Thu nhập", Legend.LegendForm.SQUARE, 10f, 2f, null, colorInfor);
        LegendEntry l2 = new LegendEntry("Chi tiêu", Legend.LegendForm.SQUARE, 10f, 2f, null, colorWarning);
        LegendEntry l3 = new LegendEntry("Lợi nhuận", Legend.LegendForm.SQUARE, 10f, 2f, null, colorSuccess);
        LegendEntry l4 = new LegendEntry("Lỗ", Legend.LegendForm.SQUARE, 10f, 2f, null, colorDanger);

        legend.setCustom(new LegendEntry[]{l1, l2, l3, l4});

        barChart.groupBars(0f, groupSpace, barSpace);
        barChart.notifyDataSetChanged();

        // KHÓA ZOOM ĐỂ CỘT KHÔNG BỊ PHÌNH
        barChart.setVisibleXRangeMaximum(visibleGroups);
        barChart.setVisibleXRangeMinimum(visibleGroups);

        float scrollPosition = Math.max(0, data.size() - visibleGroups) * groupWidth;
        barChart.moveViewToX(scrollPosition);

        barChart.invalidate();

        int lastIndex = data.size() - 1;
        Highlight defaultHighlight = new Highlight(lastIndex, 0, 0);
        barChart.highlightValue(defaultHighlight, false);
        updateListFromChartSelection(lastIndex, defaultHighlight);
    }

    private void renderStackedBarChart(List<StackedBarChartDto> data) {
        if (data == null || data.isEmpty()) {
            barChart.clear();
            return;
        }

        barChart.highlightValues(null);
        barChart.fitScreen();

        List<String> uniqueCatIds = new ArrayList<>();
        List<String> uniqueCatNames = new ArrayList<>();
        List<Integer> uniqueColors = new ArrayList<>();

        for (StackedBarChartDto periodData : data) {
            for (CategoryPieChartDto cat : periodData.getCategoryBreakdowns()) {
                if (!uniqueCatIds.contains(cat.getCategoryId())) {
                    uniqueCatIds.add(cat.getCategoryId());
                    uniqueCatNames.add(cat.getCategoryName());
                    int actualColor = AppResourceManager.getColor(cat.getColorId());
                    uniqueColors.add(actualColor);
                }
            }
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            StackedBarChartDto periodData = data.get(i);
            xLabels.add(periodData.getPeriod());

            float[] stackValues = new float[uniqueCatIds.size()];
            for (int j = 0; j < uniqueCatIds.size(); j++) {
                String targetCatId = uniqueCatIds.get(j);
                float amount = 0f;
                for (CategoryPieChartDto cat : periodData.getCategoryBreakdowns()) {
                    if (cat.getCategoryId().equals(targetCatId)) {
                        amount = (float) cat.getTotalAmount();
                        break;
                    }
                }
                stackValues[j] = amount;
            }
            entries.add(new BarEntry(i, stackValues));
        }

        BarDataSet set = new BarDataSet(entries, "");
        set.setColors(uniqueColors);
        set.setStackLabels(uniqueCatNames.toArray(new String[0]));
        set.setHighlightEnabled(true);

        BarData barData = new BarData(set);
        barData.setDrawValues(false);

        barChart.setData(barData);
        barChart.getBarData().setBarWidth(0.4f);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));

        // FIX LỆCH NHÃN: Ép CenterAxisLabels TẮT cho biểu đồ cột đơn
        barChart.getXAxis().setCenterAxisLabels(false);
        barChart.getXAxis().setAxisMinimum(-0.5f);

        int visibleGroups = 5;
        float maxX = Math.max(data.size(), visibleGroups) - 0.5f;
        barChart.getXAxis().setAxisMaximum(maxX);

        Legend legend = barChart.getLegend();
        legend.resetCustom();
        legend.setXEntrySpace(15f); // Đảm bảo Legend tự động không bị dính chùm
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);

        barChart.notifyDataSetChanged();

        // KHÓA ZOOM ĐỂ CỘT KHÔNG BỊ PHÌNH
        barChart.setVisibleXRangeMaximum(visibleGroups);
        barChart.setVisibleXRangeMinimum(visibleGroups);

        float scrollPosition = Math.max(0, data.size() - visibleGroups);
        barChart.moveViewToX(scrollPosition);

        barChart.invalidate();

        int lastIndex = data.size() - 1;
        Highlight defaultHighlight = new Highlight(lastIndex, 0, -1);
        barChart.highlightValue(defaultHighlight, false);
        updateListFromChartSelection(lastIndex, defaultHighlight);
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}