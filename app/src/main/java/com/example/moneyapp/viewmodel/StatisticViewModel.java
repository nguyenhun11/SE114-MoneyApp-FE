package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.CashFlowBarDto;
import com.example.moneyapp.data.remote.response.StackedBarChartDto;
import com.example.moneyapp.data.repository.StatisticRepository;

import java.util.Date;
import java.util.List;

public class StatisticViewModel extends AndroidViewModel {

    private final StatisticRepository repository;

    // 1. LiveData chứa dữ liệu cho Tab "Chung" (Dòng tiền)
    private final MutableLiveData<List<CashFlowBarDto>> cashFlowData = new MutableLiveData<>();

    // 2. LiveData chứa dữ liệu cho Tab "Chi" (Cột chồng Chi tiêu)
    private final MutableLiveData<List<StackedBarChartDto>> expenseStackedBarData = new MutableLiveData<>();

    // 3. LiveData chứa dữ liệu cho Tab "Thu" (Cột chồng Thu nhập)
    private final MutableLiveData<List<StackedBarChartDto>> incomeStackedBarData = new MutableLiveData<>();

    // 4. LiveData xử lý lỗi chung
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public StatisticViewModel(@NonNull Application application) {
        super(application);
        repository = new StatisticRepository(application);
    }

    // ==========================================
    // GETTERS CHO OBSERVERS
    // ==========================================
    public LiveData<List<CashFlowBarDto>> getCashFlowData() { return cashFlowData; }
    public LiveData<List<StackedBarChartDto>> getExpenseStackedBarData() { return expenseStackedBarData; }
    public LiveData<List<StackedBarChartDto>> getIncomeStackedBarData() { return incomeStackedBarData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }

    // ==========================================
    // CÁC HÀM GỌI API TỪ REPOSITORY
    // ==========================================

    // Gọi API load Dòng tiền (Tab Chung)
    public void loadCashFlow(Date startDate, Date endDate, int groupBy) {
        repository.getCashFlowBarChart(startDate, endDate, groupBy, new StatisticRepository.StatisticCallback<List<CashFlowBarDto>>() {
            @Override
            public void onSuccess(List<CashFlowBarDto> result) {
                cashFlowData.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    // Gọi API load Cột chồng Chi tiêu (Tab Chi)
    public void loadExpenseStackedBar(Date startDate, Date endDate, int groupBy) {
        repository.getExpenseStackedBarChart(startDate, endDate, groupBy, new StatisticRepository.StatisticCallback<List<StackedBarChartDto>>() {
            @Override
            public void onSuccess(List<StackedBarChartDto> result) {
                expenseStackedBarData.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    // Gọi API load Cột chồng Thu nhập (Tab Thu)
    public void loadIncomeStackedBar(Date startDate, Date endDate, int groupBy) {
        repository.getIncomeStackedBarChart(startDate, endDate, groupBy, new StatisticRepository.StatisticCallback<List<StackedBarChartDto>>() {
            @Override
            public void onSuccess(List<StackedBarChartDto> result) {
                incomeStackedBarData.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }
}