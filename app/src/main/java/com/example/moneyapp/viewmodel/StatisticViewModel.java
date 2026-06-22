package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.CashFlowBarDto;
import com.example.moneyapp.data.remote.response.StackedBarChartDto;
import com.example.moneyapp.data.repository.StatisticRepository;
import com.example.moneyapp.data.repository.TransactionRepository;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Mood;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.view.home.PieChartItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticViewModel extends AndroidViewModel {

    private final StatisticRepository repository;

    // ĐÃ THÊM: Khai báo TransactionRepository để tự tải dữ liệu
    private final TransactionRepository transactionRepository;

    // 1. LiveData chứa dữ liệu cho Tab "Chung" (Dòng tiền)
    private final MutableLiveData<List<CashFlowBarDto>> cashFlowData = new MutableLiveData<>();

    // 2. LiveData chứa dữ liệu cho Tab "Chi" (Cột chồng Chi tiêu)
    private final MutableLiveData<List<StackedBarChartDto>> expenseStackedBarData = new MutableLiveData<>();

    // 3. LiveData chứa dữ liệu cho Tab "Thu" (Cột chồng Thu nhập)
    private final MutableLiveData<List<StackedBarChartDto>> incomeStackedBarData = new MutableLiveData<>();

    // 4. LiveData xử lý lỗi chung
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<PieChartItem>> moodSpendingData = new MutableLiveData<>();

    public StatisticViewModel(@NonNull Application application) {
        super(application);
        repository = new StatisticRepository(application);

        // ĐÃ THÊM: Khởi tạo Repository
        transactionRepository = new TransactionRepository(application);
    }

    // ==========================================
    // GETTERS CHO OBSERVERS
    // ==========================================
    public LiveData<List<CashFlowBarDto>> getCashFlowData() { return cashFlowData; }
    public LiveData<List<StackedBarChartDto>> getExpenseStackedBarData() { return expenseStackedBarData; }
    public LiveData<List<StackedBarChartDto>> getIncomeStackedBarData() { return incomeStackedBarData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<List<PieChartItem>> getMoodSpendingData() { return moodSpendingData; }

    // ==========================================
    // TÍNH TOÁN CẢM XÚC
    // ==========================================
    public void calculateMoodSpending(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            moodSpendingData.postValue(new ArrayList<>());
            return;
        }

        Map<Integer, Double> totals = new HashMap<>();
        double totalAll = 0;

        for (Transaction t : transactions) {
            // Đảm bảo an toàn 2 lớp: Chỉ tính giao dịch Chi tiêu
            if (t.getType() == CategoryType.EXPENSE) {
                int moodId = t.getMoodId();
                totals.put(moodId, totals.getOrDefault(moodId, 0.0) + t.getBaseAmount());
                totalAll += t.getBaseAmount();
            }
        }

        List<PieChartItem> items = new ArrayList<>();
        if (totalAll > 0) {
            for (Mood mood : Mood.getAllMoods()) {
                Double amount = totals.get(mood.getId());
                if (amount != null && amount > 0) {
                    float percent = (float) (amount / totalAll * 100);
                    items.add(new PieChartItem(
                            String.valueOf(mood.getId()),
                            mood.getName() + " " + mood.getEmoji(),
                            amount,
                            percent,
                            mood.getColor(),
                            0));
                }
            }
        }
        moodSpendingData.postValue(items);
    }

    public void loadMoodStatistics(Date startDate, Date endDate) {
        // Tối ưu hóa: Thay vì tải NULL (tất cả), truyền hẳn CategoryType.EXPENSE để API chỉ trả về Chi tiêu cho nhẹ mạng
        transactionRepository.getFilteredTransactions(startDate, endDate, CategoryType.EXPENSE, null, null,
                new TransactionRepository.TransactionCallback<List<Transaction>>() {
                    @Override
                    public void onSuccess(List<Transaction> result) {
                        if (result != null) {
                            calculateMoodSpending(result);
                        } else {
                            calculateMoodSpending(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onError(String message) {
                        errorLiveData.postValue(message);
                    }
                });
    }

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