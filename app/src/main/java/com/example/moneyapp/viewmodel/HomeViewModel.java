package com.example.moneyapp.viewmodel;

import android.app.Application;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.CategoryPieChartDto;
import com.example.moneyapp.data.remote.response.DashboardOverviewResponse;
import com.example.moneyapp.data.remote.response.TotalBalanceDto;
import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.data.repository.DashboardRepository;
import com.example.moneyapp.data.repository.StatisticRepository;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.view.home.PieChartItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final DashboardRepository dashboardRepository;
    private final AccountRepository accountRepository;
    private final StatisticRepository statisticRepository;

    private final MutableLiveData<Double> totalBalance = new MutableLiveData<>();
    private final MutableLiveData<List<PieChartItem>> categoryExpenses = new MutableLiveData<>();
    private final MutableLiveData<Double> chartTotalAmount = new MutableLiveData<>();
    private final MutableLiveData<DashboardOverviewResponse> dashboardOverview = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private int currentTabType = 0;
    private Date currentStartDate;
    private Date currentEndDate;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        dashboardRepository = new DashboardRepository(application);
        accountRepository = new AccountRepository(application);
        statisticRepository = new StatisticRepository(application);
    }

    public LiveData<Double> getTotalBalance() { return totalBalance; }
    public LiveData<List<PieChartItem>> getCategoryExpenses() { return categoryExpenses; }
    public LiveData<Double> getChartTotalAmount() { return chartTotalAmount; }
    public LiveData<DashboardOverviewResponse> getDashboardOverview() { return dashboardOverview; }
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

        accountRepository.getTotalBalance(new AccountRepository.AccountCallback<java.util.Map<String, TotalBalanceDto>>() {
            @Override
            public void onSuccess(java.util.Map<String, TotalBalanceDto> result) {
                double totalBaseAmount = 0.0;
                String systemCurrency = "VND";

                if (result != null) {
                    for (java.util.Map.Entry<String, TotalBalanceDto> entry : result.entrySet()) {
                        String currency = entry.getKey();
                        TotalBalanceDto dto = entry.getValue();
                        double amount = dto.getAvailableBalance();

                        totalBaseAmount += amount * getMockExchangeRate(currency, systemCurrency);
                    }
                }
                totalBalance.postValue(totalBaseAmount);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
            }
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

    public void fetchDashboardOverview() {
        isLoading.setValue(true);
        dashboardRepository.getDashboardOverview(new DashboardRepository.DashboardCallback() {
            @Override
            public void onSuccess(DashboardOverviewResponse result) {
                dashboardOverview.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private double getMockExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) return 1.0;
        if (fromCurrency.equals("USD") && toCurrency.equals("VND")) return 25000.0;
        if (fromCurrency.equals("EUR") && toCurrency.equals("VND")) return 27000.0;
        if (fromCurrency.equals("JPY") && toCurrency.equals("VND")) return 160.0;
        return 1.0;
    }
}