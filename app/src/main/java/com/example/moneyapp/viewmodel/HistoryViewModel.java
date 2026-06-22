package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.repository.AdjustBalanceRepository;
import com.example.moneyapp.data.repository.TransactionRepository;
import com.example.moneyapp.data.repository.TransferRepository;
import com.example.moneyapp.model.AdjustBalance;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.DailyTransactionGroup;
import com.example.moneyapp.model.HistoryItem;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.model.Transfer;
import com.example.moneyapp.utils.CurrencyFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryViewModel extends AndroidViewModel {
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private final AdjustBalanceRepository adjustBalanceRepository;

    private final MutableLiveData<List<DailyTransactionGroup>> groupedTransactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private Date currentStartDate;
    private Date currentEndDate;
    private CategoryType currentType = null;
    private String currentAccountId = null;
    private String currentCategoryId = null;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        transferRepository = new TransferRepository(application);
        adjustBalanceRepository = new AdjustBalanceRepository(application);
    }

    public LiveData<List<DailyTransactionGroup>> getGroupedTransactions() { return groupedTransactionsLiveData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void setTimeRangeAndReload(Date start, Date end) {
        this.currentStartDate = start;
        this.currentEndDate = end;
        reloadTransactions();
    }

    public void setTypeAndReload(CategoryType type) {
        this.currentType = type;
        reloadTransactions();
    }

    public void setAccountFilterAndReload(String accountId) {
        this.currentAccountId = accountId;
        reloadTransactions();
    }

    public void setCategoryFilterAndReload(String categoryId) {
        this.currentCategoryId = categoryId;
        reloadTransactions();
    }

    public void reloadTransactions() {
        if (currentStartDate == null || currentEndDate == null) return;
        loadTransactions(currentStartDate, currentEndDate, currentType, currentAccountId, currentCategoryId);
    }

    public void loadTransactions(Date start, Date end, CategoryType type, String accountId, String categoryId) {
        isLoading.setValue(true);

        List<HistoryItem> mergedList = new ArrayList<>();
        final int TOTAL_APIS_TO_CALL = 3;
        final int[] completedCalls = {0};

        Runnable checkAllDone = () -> {
            completedCalls[0]++;
            if (completedCalls[0] == TOTAL_APIS_TO_CALL) {
                Collections.sort(mergedList, (item1, item2) -> {
                    Date d1 = item1.getDate();
                    Date d2 = item2.getDate();
                    if (d1 == null || d2 == null) return 0;
                    return d2.compareTo(d1); // Descending
                });

                groupedTransactionsLiveData.postValue(groupTransactionsByDate(mergedList));
                isLoading.postValue(false);
            }
        };

        transactionRepository.getFilteredTransactions(start, end, type, accountId, categoryId, new TransactionRepository.TransactionCallback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> result) {
                if (result != null) {
                    for (Transaction t : result) mergedList.add(new HistoryItem(t));
                }
                checkAllDone.run();
            }
            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                checkAllDone.run();
            }
        });

        if (type == null) {
            transferRepository.getTransfers(start, end, accountId, null, new TransferRepository.TransferCallback<List<Transfer>>() {
                @Override
                public void onSuccess(List<Transfer> result) {
                    if (result != null) {
                        for (Transfer t : result) mergedList.add(new HistoryItem(t));
                    }
                    checkAllDone.run();
                }

                @Override
                public void onError(String message) {
                    checkAllDone.run();
                }
            });

            adjustBalanceRepository.getAdjustBalances(start, end, accountId, new AdjustBalanceRepository.AdjustBalanceCallback<List<AdjustBalance>>() {
                @Override
                public void onSuccess(List<AdjustBalance> result) {
                    if (result != null) {
                        for (AdjustBalance ab : result) mergedList.add(new HistoryItem(ab));
                    }
                    checkAllDone.run();
                }

                @Override
                public void onError(String message) {
                    checkAllDone.run();
                }
            });
        } else {
            // Nếu đang lọc loại Thu hoặc Chi thì không tải Transfer và AdjustBalance
            checkAllDone.run();
            checkAllDone.run();
        }
    }

    private List<DailyTransactionGroup> groupTransactionsByDate(List<HistoryItem> historyItems) {
        if (historyItems == null || historyItems.isEmpty()) return new ArrayList<>();

        Map<String, List<HistoryItem>> groupedMap = new LinkedHashMap<>();
        for (HistoryItem item : historyItems) {
            String dateKey = formatToDisplayDate(item.getDate());
            if (!groupedMap.containsKey(dateKey)) {
                groupedMap.put(dateKey, new ArrayList<>());
            }
            groupedMap.get(dateKey).add(item);
        }

        List<DailyTransactionGroup> resultList = new ArrayList<>();

        for (Map.Entry<String, List<HistoryItem>> entry : groupedMap.entrySet()) {
            double totalDayBaseAmount = 0;

            for (HistoryItem item : entry.getValue()) {
                if (item.getType() == HistoryItem.TYPE_TRANSACTION && item.getTransaction() != null) {
                    Transaction t = item.getTransaction();
                    double amountToAdd = t.getBaseAmount() != null ? t.getBaseAmount() : 0.0;
                    if (t.getType() == CategoryType.EXPENSE) {
                        totalDayBaseAmount -= amountToAdd;
                    } else {
                        totalDayBaseAmount += amountToAdd;
                    }
                }
                else if (item.getType() == HistoryItem.TYPE_ADJUST_BALANCE && item.getAdjustBalance() != null) {
                    totalDayBaseAmount += item.getAdjustBalance().getAmount();
                }
            }

            String sign = totalDayBaseAmount >= 0 ? "+" : "-";
            String dateSummary = String.format("%s %s", sign, CurrencyFormatter.formatVND(Math.abs(totalDayBaseAmount)));

            resultList.add(new DailyTransactionGroup(entry.getKey(), dateSummary, entry.getValue()));
        }

        return resultList;
    }

    private String formatToDisplayDate(Date date) {
        if (date == null) return "Chưa xác định";
        java.text.DateFormat formatter = java.text.DateFormat.getDateInstance(
                java.text.DateFormat.LONG, Locale.getDefault());
        return formatter.format(date);
    }

    public String getCurrentAccountId() { return currentAccountId; }
    public String getCurrentCategoryId() { return currentCategoryId; }
}