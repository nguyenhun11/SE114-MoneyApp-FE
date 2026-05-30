package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.CashFlowBarDto;
import com.example.moneyapp.data.remote.response.CategoryPieChartDto;
import com.example.moneyapp.data.remote.response.StackedBarChartDto;
import com.example.moneyapp.data.repository.StatisticRepository;

import java.util.Date;
import java.util.List;

public class StatisticViewModel extends AndroidViewModel {
    private final StatisticRepository repository;
    
    private final MutableLiveData<List<CategoryPieChartDto>> expensePieData = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryPieChartDto>> incomePieData = new MutableLiveData<>();
    private final MutableLiveData<List<StackedBarChartDto>> expenseBarData = new MutableLiveData<>();
    private final MutableLiveData<List<CashFlowBarDto>> cashFlowData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public StatisticViewModel(@NonNull Application application) {
        super(application);
        repository = new StatisticRepository(application);
    }

    public LiveData<List<CategoryPieChartDto>> getExpensePieData() { return expensePieData; }
    public LiveData<List<CategoryPieChartDto>> getIncomePieData() { return incomePieData; }
    public LiveData<List<StackedBarChartDto>> getExpenseBarData() { return expenseBarData; }
    public LiveData<List<CashFlowBarDto>> getCashFlowData() { return cashFlowData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }

    public void loadExpensePie(Date start, Date end) {
        repository.getExpensePieChart(start, end, new StatisticRepository.StatisticCallback<List<CategoryPieChartDto>>() {
            @Override
            public void onSuccess(List<CategoryPieChartDto> result) { expensePieData.postValue(result); }
            @Override
            public void onError(String message) { errorLiveData.postValue(message); }
        });
    }

    public void loadIncomePie(Date start, Date end) {
        repository.getIncomePieChart(start, end, new StatisticRepository.StatisticCallback<List<CategoryPieChartDto>>() {
            @Override
            public void onSuccess(List<CategoryPieChartDto> result) { incomePieData.postValue(result); }
            @Override
            public void onError(String message) { errorLiveData.postValue(message); }
        });
    }

    public void loadExpenseBar(Date start, Date end, int groupBy) {
        repository.getExpenseStackedBarChart(start, end, groupBy, new StatisticRepository.StatisticCallback<List<StackedBarChartDto>>() {
            @Override
            public void onSuccess(List<StackedBarChartDto> result) { expenseBarData.postValue(result); }
            @Override
            public void onError(String message) { errorLiveData.postValue(message); }
        });
    }

    public void loadCashFlow(Date start, Date end, int groupBy) {
        repository.getCashFlowBarChart(start, end, groupBy, new StatisticRepository.StatisticCallback<List<CashFlowBarDto>>() {
            @Override
            public void onSuccess(List<CashFlowBarDto> result) { cashFlowData.postValue(result); }
            @Override
            public void onError(String message) { errorLiveData.postValue(message); }
        });
    }
}
