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
    private final MutableLiveData<Double> chartTotalAmount = new MutableLiveData<>(); // LiveData cho số tiền giữa biểu đồ
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        statisticRepository = new StatisticRepository(application);
    }

    public LiveData<Double> getTotalBalance() {
        return totalBalance;
    }

    public LiveData<List<PieChartItem>> getCategoryExpenses() {
        return categoryExpenses;
    }

    public LiveData<Double> getChartTotalAmount() {
        return chartTotalAmount;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadHomeData() {
        isLoading.setValue(true);

        // 1. Tải Tổng số dư chung (Total Balance)
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

//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.DAY_OF_MONTH, 1); // Set về ngày đầu tháng
//        Date startDate = cal.getTime();
//        Date endDate = new Date();

        // Ép startDate về 01/01/2025
        Calendar calStart = Calendar.getInstance();
        calStart.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
        Date startDate = calStart.getTime();

// Ép endDate về 31/12/2025
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2025, Calendar.DECEMBER, 31, 23, 59, 59);
        Date endDate = calEnd.getTime();

        statisticRepository.getExpensePieChart(startDate, endDate, new StatisticRepository.StatisticCallback<List<CategoryPieChartDto>>() {
            @Override
            public void onSuccess(List<CategoryPieChartDto> result) {
                List<PieChartItem> list = new ArrayList<>();
                double sumTotal = 0.0;

                for (CategoryPieChartDto dto : result) {
                    // Ánh xạ ID màu từ DB sang mã màu Android
                    int androidColor = mapColorIdToAndroidColor(dto.getColorId());

                    // Đóng gói thành UI Model (kèm ID để phục vụ tương tác Click)
                    list.add(new PieChartItem(dto.getCategoryId(), // Chìa khóa tương tác
                            dto.getCategoryName(), dto.getTotalAmount(), (float) dto.getPercentage(), androidColor));

                    sumTotal += dto.getTotalAmount();
                }

                categoryExpenses.postValue(list);
                chartTotalAmount.postValue(sumTotal); // Bắn tổng tiền ra UI
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    // ======================================================================
    // HELPER: Hàm chuyển đổi ColorId từ DB (.NET) sang mã màu hiển thị trên Android
    // (Ông có thể tách hàm này ra file ColorHelper.java dùng chung cho gọn)
    // ======================================================================
    private int mapColorIdToAndroidColor(int colorId) {
        switch (colorId) {
            case 1:
                return Color.parseColor("#FFB300"); // Vàng cam
            case 2:
                return Color.parseColor("#FF3D57"); // Đỏ hồng
            case 3:
                return Color.parseColor("#7C4DFF"); // Tím
            case 4:
                return Color.parseColor("#00E676"); // Xanh lá
            case 5:
                return Color.parseColor("#29B6F6"); // Xanh dương
            case 6:
                return Color.parseColor("#FF7043"); // Cam san hô
            case 7:
                return Color.parseColor("#EC407A"); // Hồng phấn
            default:
                return Color.parseColor("#9E9E9E"); // Xám (Fallback)
        }
    }
}