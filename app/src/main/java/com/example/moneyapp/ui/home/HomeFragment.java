package com.example.moneyapp.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.category.CategoryExpenseAdapter;
import com.example.moneyapp.ui.BaseFragment;
import com.example.moneyapp.ui.home.PieChartItem;
import com.example.moneyapp.viewmodel.HomeViewModel;
import com.github.mikephil.charting.charts.HorizontalBarChart; // Của MPAndroidChart
import com.github.mikephil.charting.charts.PieChart; // Của MPAndroidChart
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategoryExpenseAdapter adapter;

    // View Containers
    private View pieChartContainer;
    private View linearChartContainer;
    private AppBarLayout appBarLayout;

    // Biểu đồ
    private PieChart pieChart;
    private HorizontalBarChart linearChart;
    private TextView tvTotalAmountPie; // Chữ số tiền to ở giữa PieChart

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

        // Ánh xạ View
        rvCategories = view.findViewById(R.id.rv_categories);
        pieChartContainer = view.findViewById(R.id.pie_chart_container);
        linearChartContainer = view.findViewById(R.id.linear_chart_container);
        appBarLayout = view.findViewById(R.id.app_bar);

        // Ánh xạ Biểu đồ (Giả sử ID trong XML của ông đặt như vầy)
        pieChart = view.findViewById(R.id.main_pie_chart);
        linearChart = view.findViewById(R.id.main_linear_chart);
        tvTotalAmountPie = view.findViewById(R.id.tv_total_amount_pie);

        setupRecyclerView();
        setupPieChart();
        setupLinearChart();
        setupScrollBehavior();

        observeViewModel();

        homeViewModel.loadHomeData();
    }

    private void setupRecyclerView() {
        // Lưu ý: Đảm bảo CategoryExpenseAdapter của ông ĐÃ ĐỔI sang nhận List<PieChartItem>
        adapter = new CategoryExpenseAdapter(new ArrayList<>());
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setAdapter(adapter);
    }

    // Cấu hình UI tĩnh cho PieChart (Biểu đồ tròn to)
    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false); // Tắt chữ description mặc định
        pieChart.setDrawHoleEnabled(true); // Bật chế độ đục lỗ ở giữa (Donut chart)
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(0f); // Tắt viền mờ
        pieChart.setDrawEntryLabels(false); // Ẩn chữ nhãn đè lên các miếng bánh

        // Vì số tiền tổng ở giữa ta xài TextView riêng chèn đè lên cho dễ custom font (như bản thiết kế)
        // Nên ta sẽ tắt chữ Text ở giữa lỗ của PieChart đi
        pieChart.setDrawCenterText(false);

        Legend l = pieChart.getLegend();
        l.setEnabled(false); // Ẩn chú thích bên cạnh vì đã có RecyclerView ở dưới rồi
    }

    // Cấu hình UI tĩnh cho LinearChart (Biểu đồ thanh ngang nhỏ khi cuộn lên)
    private void setupLinearChart() {
        linearChart.getDescription().setEnabled(false);
        linearChart.setDrawGridBackground(false);
        linearChart.setDrawBorders(false);

        // Ẩn toàn bộ trục tung, trục hoành, lưới kẻ
        linearChart.getAxisLeft().setEnabled(false);
        linearChart.getAxisRight().setEnabled(false);
        linearChart.getXAxis().setEnabled(false);
        linearChart.getLegend().setEnabled(false);
        linearChart.setTouchEnabled(false); // Khóa tương tác vì nó nhỏ quá
    }

    private void observeViewModel() {
        // Cập nhật số dư tổng (Top Bar)
        homeViewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            setupBalanceSelector(requireView(), getString(R.string.total_balance),
                    String.format("%,.0f", balance).replace(",", "."), true);
        });

        // 🌟 Cập nhật danh sách và vẽ biểu đồ 🌟
        homeViewModel.getCategoryExpenses().observe(getViewLifecycleOwner(), items -> {
            // 1. Cập nhật RecyclerView (Danh sách tóm tắt)
            adapter.updateData(items);

            // 2. Vẽ 2 cái biểu đồ
            populateCharts(items);
        });

        // Cập nhật chữ số tiền ở giữa biểu đồ tròn
        homeViewModel.getChartTotalAmount().observe(getViewLifecycleOwner(), total -> {
            if (tvTotalAmountPie != null) {
                tvTotalAmountPie.setText(String.format("%,.0f", total).replace(",", "."));
            }
        });

        homeViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm thực hiện việc đẩy Data vào Chart
    private void populateCharts(List<PieChartItem> items) {
        if (items == null || items.isEmpty()) {
            pieChart.clear();
            linearChart.clear();
            return;
        }

        // ================= ĐỔ DỮ LIỆU VÀO PIE CHART =================
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        // Biến dùng chung cho Bar Chart
        float currentStackedValue = 0f;
        float[] barValues = new float[items.size()];

        for (int i = 0; i < items.size(); i++) {
            PieChartItem item = items.get(i);

            // Dữ liệu cho PieChart
            pieEntries.add(new PieEntry(item.getPercentage(), item.getName()));
            colors.add(item.getColor());

            // Dữ liệu cho Linear (BarChart chồng - Stacked Bar)
            barValues[i] = item.getPercentage();
        }

        // Tạo Dataset cho PieChart
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setDrawValues(false); // Ẩn số % hiển thị đè lên biểu đồ cho gọn
        pieDataSet.setSelectionShift(5f); // Hiệu ứng phình to khi click

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // Lệnh này bắt buộc gọi để làm mới giao diện

        // ================= ĐỔ DỮ LIỆU VÀO LINEAR CHART =================
        // Dùng Stacked Bar Chart (Thanh ngang chồng lên nhau) để tạo ra dải màu
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, barValues)); // Chỉ có 1 thanh nằm ngang duy nhất

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(colors); // Dùng chung mảng màu với PieChart
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f); // Chỉnh độ dày của thanh

        linearChart.setData(barData);
        linearChart.invalidate();
    }

    // Hiệu ứng cuộn mượt mà do bạn ông viết (Giữ nguyên)
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