package com.example.moneyapp.viewmodel;

import android.app.Application;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.CategoryPieChartDto;
import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.data.repository.StatisticRepository;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.view.home.PieChartItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final AccountRepository accountRepository;
    private final StatisticRepository statisticRepository;

    private final MutableLiveData<Double> totalBalance = new MutableLiveData<>();
    private final MutableLiveData<List<PieChartItem>> categoryExpenses = new MutableLiveData<>();
    private final MutableLiveData<Double> chartTotalAmount = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private int currentTabType = 0;
    private Date currentStartDate;
    private Date currentEndDate;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        statisticRepository = new StatisticRepository(application);
    }

    public LiveData<Double> getTotalBalance() { return totalBalance; }
    public LiveData<List<PieChartItem>> getCategoryExpenses() { return categoryExpenses; }
    public LiveData<Double> getChartTotalAmount() { return chartTotalAmount; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void setTimeRangeAndReload(Date startDate, Date endDate) {
        this.currentStartDate = startDate;
        this.currentEndDate = endDate;
        loadHomeData();
    }

    public void setTabTypeAndReload(int type) {
        this.currentTabType = type;
        loadHomeData();
    }

    private void loadHomeData() {
        if (currentStartDate == null || currentEndDate == null) return;
        android.util.Log.d("BUG_TRACKING", "Bắt đầu gọi API." +
                "\nTab: " + (currentTabType == 0 ? "Chi tiêu" : "Thu nhập") +
                "\nTừ ngày: " + currentStartDate.toString() +
                "\nĐến ngày: " + currentEndDate.toString());

        isLoading.setValue(true);

        accountRepository.getTotalBalance(new AccountRepository.AccountCallback<Double>() {
            @Override
            public void onSuccess(Double result) { totalBalance.postValue(result); }
            @Override
            public void onError(String message) { error.postValue(message); }
        });

        StatisticRepository.StatisticCallback<List<CategoryPieChartDto>> callback = new StatisticRepository.StatisticCallback<List<CategoryPieChartDto>>() {
            @Override
            public void onSuccess(List<CategoryPieChartDto> result) {
                List<PieChartItem> list = new ArrayList<>();
                double sumTotal = 0.0;

                for (CategoryPieChartDto dto : result) {
                    int androidColor = AppResourceManager.getColor(dto.getColorId());

                    list.add(new PieChartItem(
                            dto.getCategoryId(),
                            dto.getCategoryName(),
                            dto.getTotalAmount(),
                            (float) dto.getPercentage(),
                            androidColor,
                            dto.getIconId()
                    ));
                    sumTotal += dto.getTotalAmount();
                }

                categoryExpenses.postValue(list);
                chartTotalAmount.postValue(sumTotal);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        };

        if (currentTabType == 0) {
            statisticRepository.getExpensePieChart(currentStartDate, currentEndDate, callback);
        } else {
            statisticRepository.getIncomePieChart(currentStartDate, currentEndDate, callback);
        }
    }
}