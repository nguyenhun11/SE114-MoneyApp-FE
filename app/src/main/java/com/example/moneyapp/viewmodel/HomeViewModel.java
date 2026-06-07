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
import com.example.moneyapp.view.home.PieChartItem;

import java.util.ArrayList;
import java.util.Calendar;
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

    public void setTabTypeAndReload(int type) {
        this.currentTabType = type;
        loadHomeData();
    }

    public void loadHomeData() {
        isLoading.setValue(true);

        accountRepository.getTotalBalance(new AccountRepository.AccountCallback<Double>() {
            @Override
            public void onSuccess(Double result) {
                totalBalance.postValue(result);
            }
            @Override
            public void onError(String message) {
                error.postValue(message);
            }
        });

        // TODO: Lấy tạm dữ liệu năm 2025
        Calendar calStart = Calendar.getInstance();
        calStart.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
        Date startDate = calStart.getTime();

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2025, Calendar.DECEMBER, 31, 23, 59, 59);
        Date endDate = calEnd.getTime();

        StatisticRepository.StatisticCallback<List<CategoryPieChartDto>> callback = new StatisticRepository.StatisticCallback<List<CategoryPieChartDto>>() {
            @Override
            public void onSuccess(List<CategoryPieChartDto> result) {
                List<PieChartItem> list = new ArrayList<>();
                double sumTotal = 0.0;

                for (CategoryPieChartDto dto : result) {
                    int androidColor = mapColorIdToAndroidColor(dto.getColorId());
                    list.add(new PieChartItem(dto.getCategoryId(), dto.getCategoryName(), dto.getTotalAmount(), (float) dto.getPercentage(), androidColor));
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
            statisticRepository.getExpensePieChart(startDate, endDate, callback);
        } else {
            statisticRepository.getIncomePieChart(startDate, endDate, callback);
        }
    }

    private int mapColorIdToAndroidColor(int colorId) {
        switch (colorId) {
            case 1: return Color.parseColor("#FFB300");
            case 2: return Color.parseColor("#FF3D57");
            case 3: return Color.parseColor("#7C4DFF");
            case 4: return Color.parseColor("#00E676");
            case 5: return Color.parseColor("#29B6F6");
            case 6: return Color.parseColor("#FF7043");
            case 7: return Color.parseColor("#EC407A");
            default: return Color.parseColor("#9E9E9E");
        }
    }
}