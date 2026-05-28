package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.CategoryPieChartDto;
import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.data.repository.StatisticRepository;
import com.example.moneyapp.ui.models.PieChartItem; // Dùng model UI chuẩn
import com.example.moneyapp.utils.ColorHelper; // Giả sử ông có class này

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final AccountRepository accountRepository;
    private final StatisticRepository statisticRepository;

    private final MutableLiveData<Double> totalBalance = new MutableLiveData<>();
    private final MutableLiveData<List<PieChartItem>> categoryExpenses = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        statisticRepository = new StatisticRepository(application);
    }

    public LiveData<Double> getTotalBalance() { return totalBalance; }
    public LiveData<List<PieChartItem>> getCategoryExpenses() { return categoryExpenses; }
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

        // 2. Load Expense Pie Chart cho tháng hiện tại
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        Date endDate = new Date();

        statisticRepository.getExpensePieChart(startDate, endDate, new StatisticRepository.StatisticCallback<List<CategoryPieChartDto>>() {
            @Override
            public void onSuccess(List<CategoryPieChartDto> result) {
                List<PieChartItem> list = new ArrayList<>();
                for (CategoryPieChartDto dto : result) {

                    // 🌟 SỬA LỖI MÀU SẮC: Chuyển đổi ID màu từ DB sang mã màu Android
                    int androidColor = ColorHelper.getColorFromId(dto.getColorId());

                    list.add(new PieChartItem(
                            dto.getCategoryName(),
                            dto.getTotalAmount(), // 🌟 SỬA LỖI TÊN BIẾN (Giữ nguyên double để format tiền tệ dưới UI)
                            (float) dto.getPercentage(), // Ép kiểu float cho thư viện biểu đồ
                            androidColor
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