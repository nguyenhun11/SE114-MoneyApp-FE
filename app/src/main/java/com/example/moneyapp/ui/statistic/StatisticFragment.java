package com.example.moneyapp.ui.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;
import com.example.moneyapp.viewmodel.StatisticViewModel;

import java.util.Calendar;
import java.util.Date;

public class StatisticFragment extends BaseFragment {

    private StatisticViewModel statisticViewModel;
    private int currentTab = 0; // 0: Chung, 1: Chi, 2: Thu

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
        
        setupThreeTabs(view, index -> {
            currentTab = index;
            loadDataByTab();
        });

        observeViewModel();
        loadDataByTab();
    }

    private void observeViewModel() {
        // Observe dữ liệu biểu đồ tròn chi tiêu
        statisticViewModel.getExpensePieData().observe(getViewLifecycleOwner(), data -> {
            if (currentTab == 1) { // Tab Chi
                // Cập nhật PieChart của bạn với data (List<CategoryPieChartDto>)
            }
        });

        // Observe dữ liệu biểu đồ tròn thu nhập
        statisticViewModel.getIncomePieData().observe(getViewLifecycleOwner(), data -> {
            if (currentTab == 2) { // Tab Thu
                // Cập nhật PieChart của bạn với data
            }
        });

        // Observe dữ liệu dòng tiền (Tab Chung)
        statisticViewModel.getCashFlowData().observe(getViewLifecycleOwner(), data -> {
            if (currentTab == 0) {
                // Cập nhật BarChart dòng tiền
            }
        });

        statisticViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataByTab() {
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();

        switch (currentTab) {
            case 0: // Chung
                statisticViewModel.loadCashFlow(startDate, endDate, 1); // 1: Group by Day
                break;
            case 1: // Chi
                statisticViewModel.loadExpensePie(startDate, endDate);
                statisticViewModel.loadExpenseBar(startDate, endDate, 1);
                break;
            case 2: // Thu
                statisticViewModel.loadIncomePie(startDate, endDate);
                break;
        }
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return true;
    }
}
