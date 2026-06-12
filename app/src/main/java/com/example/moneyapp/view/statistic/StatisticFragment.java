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
import com.example.moneyapp.utils.ResourceMapper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.CustomMarkerView;
import com.example.moneyapp.view.components.StatisticTimeSelectorView;
import com.example.moneyapp.viewmodel.StatisticViewModel;
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
    private int currentTab = 0;

    private Date currentStartDate;
    private Date currentEndDate;

    private BarChart barChart;
    private StatisticTimeSelectorView timeSelector;
    private int currentGroupBy = 2; // Mặc định là 2 (Tháng)
    private RecyclerView rvStatisticDetails;

    // TODO: Khai báo Adapter
    // private CategorySummaryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statisticViewModel = new ViewModelProvider(this).get(StatisticViewModel.class);

        setupHeader(view, R.string.stats_screen_title, false);

        barChart = view.findViewById(R.id.barChartCashFlow);
        timeSelector = view.findViewById(R.id.time_selector_stat);
        rvStatisticDetails = view.findViewById(R.id.rv_statistic_details);

        setupRecyclerView();
        setupBarChartStyle();

        setupThreeTabs(view, index -> {
            currentTab = index;
            loadDataByTab();
        });

        timeSelector.setOnTimeRangeChangeListener((startDate, endDate, groupBy) -> {
            currentStartDate = startDate;
            currentEndDate = endDate;
            currentGroupBy = groupBy; // Lấy giá trị động từ Selector
            loadDataByTab();
        });

        observeViewModel();
    }

    private void setupRecyclerView() {
        rvStatisticDetails.setLayoutManager(new LinearLayoutManager(getContext()));
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
        xAxis.setCenterAxisLabels(true);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);

        CustomMarkerView mv = new CustomMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(barChart);
        barChart.setMarker(mv);

        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);

        int colorSuccess = ContextCompat.getColor(requireContext(), R.color.colorSuccess);
        int colorDanger = ContextCompat.getColor(requireContext(), R.color.colorDanger);
        int colorInfor = ContextCompat.getColor(requireContext(), R.color.colorInfo);
        int colorWarning = ContextCompat.getColor(requireContext(), R.color.colorWarning);

        LegendEntry l1 = new LegendEntry("Thu nhập", Legend.LegendForm.SQUARE, 10f, 2f, null, colorInfor);
        LegendEntry l2 = new LegendEntry("Chi tiêu", Legend.LegendForm.SQUARE, 10f, 2f, null, colorWarning);
        LegendEntry l3 = new LegendEntry("Lợi nhuận", Legend.LegendForm.SQUARE, 10f, 2f, null, colorSuccess);
        LegendEntry l4 = new LegendEntry("Lỗ", Legend.LegendForm.SQUARE, 10f, 2f, null, colorDanger);

        legend.setCustom(new LegendEntry[]{l1, l2, l3, l4});

        barChart.animateY(1000);
    }

    private void observeViewModel() {
        statisticViewModel.getCashFlowData().observe(getViewLifecycleOwner(), data -> {
            if (currentTab == 0 && data != null) {
                renderGroupedBarChart(data);
            }
        });

        // THÊM: Lắng nghe Cột Chồng Chi Tiêu
        statisticViewModel.getExpenseStackedBarData().observe(getViewLifecycleOwner(), data -> {
            if (currentTab == 1 && data != null) {
                renderStackedBarChart(data);
            }
        });

        // THÊM: Lắng nghe Cột Chồng Thu Nhập
        statisticViewModel.getIncomeStackedBarData().observe(getViewLifecycleOwner(), data -> {
            if (currentTab == 2 && data != null) {
                renderStackedBarChart(data);
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

        switch (currentTab) {
            case 0: // Chung (Dòng tiền)
                barChart.setVisibility(View.VISIBLE);
                statisticViewModel.loadCashFlow(currentStartDate, currentEndDate, currentGroupBy);
                break;
            case 1: // Chi
                barChart.setVisibility(View.VISIBLE);
                statisticViewModel.loadExpenseStackedBar(currentStartDate, currentEndDate, currentGroupBy);
                break;
            case 2: // Thu
                barChart.setVisibility(View.VISIBLE);
                statisticViewModel.loadIncomeStackedBar(currentStartDate, currentEndDate, currentGroupBy);
                break;
        }
    }

    private void renderGroupedBarChart(List<CashFlowBarDto> data) {
        if (data == null || data.isEmpty()) {
            barChart.clear();
            return;
        }

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

            if (item.getNetBalance() >= 0) {
                balanceColors.add(colorSuccess);
            } else {
                balanceColors.add(colorDanger);
            }

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

        float groupSpace = 0.25f;
        float barSpace = 0.05f;
        float barWidth = 0.2f;

        barChart.setData(barData);
        barChart.getBarData().setBarWidth(barWidth);

        float groupWidth = barChart.getBarData().getGroupWidth(groupSpace, barSpace);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));
        barChart.getXAxis().setAxisMinimum(0f);

        // Luôn chừa đủ không gian cho ít nhất 5 nhóm
        int visibleGroups = 5;
        float maxX = Math.max(data.size(), visibleGroups) * groupWidth;
        barChart.getXAxis().setAxisMaximum(maxX);

        // Group các cột lại với nhau
        barChart.groupBars(0f, groupSpace, barSpace);

        // 2. RẤT QUAN TRỌNG: Ép biểu đồ tính toán lại mọi kích thước sau khi groupBars
        barChart.notifyDataSetChanged();

        // 3. Khóa zoom và kéo view tới vị trí dữ liệu mới nhất
        barChart.setVisibleXRangeMaximum(visibleGroups);
        float scrollPosition = Math.max(0, data.size() - visibleGroups) * groupWidth;
        barChart.moveViewToX(scrollPosition);

        barChart.invalidate(); // Vẽ lại
    }

    private void renderStackedBarChart(List<StackedBarChartDto> data) {
        if (data == null || data.isEmpty()) {
            barChart.clear();
            return;
        }

        barChart.fitScreen(); // Reset zoom cũ

        // 1. Quét tìm toàn bộ các Hạng mục ĐỘC NHẤT
        List<String> uniqueCatIds = new ArrayList<>();
        List<String> uniqueCatNames = new ArrayList<>();
        List<Integer> uniqueColors = new ArrayList<>(); // Đây sẽ là nơi chứa MÃ MÀU THẬT SỰ

        for (StackedBarChartDto periodData : data) {
            for (CategoryPieChartDto cat : periodData.getCategoryBreakdowns()) {
                if (!uniqueCatIds.contains(cat.getCategoryId())) {
                    uniqueCatIds.add(cat.getCategoryId());
                    uniqueCatNames.add(cat.getCategoryName());

                    // CHỖ SỬA ĐÂY: Dịch Resource ID thành Color Int
                    int colorResId = ResourceMapper.getColorResourceById(cat.getColorId());
                    int actualColor = ContextCompat.getColor(requireContext(), colorResId);
                    uniqueColors.add(actualColor);
                }
            }
        }

        // 2. Lắp ráp dữ liệu cho biểu đồ Cột Chồng
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

        // 3. Khởi tạo DataSet
        BarDataSet set = new BarDataSet(entries, "");
        set.setColors(uniqueColors); // Bây giờ nó mới nhận đúng mảng màu Int
        set.setStackLabels(uniqueCatNames.toArray(new String[0]));
        set.setHighlightEnabled(true);

        BarData barData = new BarData(set);
        barData.setDrawValues(false);

        barChart.setData(barData);
        barChart.getBarData().setBarWidth(0.4f);

        // 4. Khóa trục X
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));
        barChart.getXAxis().setAxisMinimum(-0.5f);

        int visibleGroups = 5;
        float maxX = Math.max(data.size(), visibleGroups) - 0.5f;
        barChart.getXAxis().setAxisMaximum(maxX);

        Legend legend = barChart.getLegend();
        legend.setCustom(new LegendEntry[0]);
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);

        barChart.notifyDataSetChanged();
        barChart.setVisibleXRangeMaximum(visibleGroups);

        float scrollPosition = Math.max(0, data.size() - visibleGroups);
        barChart.moveViewToX(scrollPosition);

        barChart.invalidate();
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}