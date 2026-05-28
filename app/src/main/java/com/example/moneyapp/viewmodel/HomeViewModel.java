package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.CategoryPieChartDto;
import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.data.repository.StatisticRepository;
import com.example.moneyapp.model.CategoryExpense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final AccountRepository accountRepository;
    private final StatisticRepository statisticRepository;

    private final MutableLiveData<Double> totalBalance = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryExpense>> categoryExpenses = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        statisticRepository = new StatisticRepository(application);
    }

    public LiveData<Double> getTotalBalance() { return totalBalance; }
    public LiveData<List<CategoryExpense>> getCategoryExpenses() { return categoryExpenses; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadHomeData() {
        isLoading.setValue(true);
        
        // 1. Load Total Balance
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

        // 2. Load Expense Pie Chart for current month
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        Date endDate = new Date();

        statisticRepository.getExpensePieChart(startDate, endDate, new StatisticRepository.StatisticCallback<List<CategoryPieChartDto>>() {
            @Override
            public void onSuccess(List<CategoryPieChartDto> result) {
                List<CategoryExpense> list = new ArrayList<>();
                for (CategoryPieChartDto dto : result) {
                    list.add(new CategoryExpense(
                            dto.getCategoryName(),
                            dto.getAmount().longValue(),
                            (float) dto.getPercentage(),
                            android.graphics.Color.parseColor(dto.getColorId() != null ? dto.getColorId() : "#9E9E9E")
                    ));
                }
                categoryExpenses.postValue(list);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }
}
