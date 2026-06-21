package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.request.CheckInRequest;
import com.example.moneyapp.data.remote.response.CheckInResponse;
import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.data.repository.TransactionRepository;
import com.example.moneyapp.data.repository.UserRepository;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.DailyTransactionGroup;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.utils.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<Double> totalBalance = new MutableLiveData<>();
    private final MutableLiveData<List<DailyTransactionGroup>> groupedTransactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Transaction>> transactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Transaction> selectedTransaction = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> checkInMessageLiveData = new MutableLiveData<>();

    private Date currentStartDate;
    private Date currentEndDate;
    private CategoryType currentType = null;
    private String currentAccountId = null;
    private String currentCategoryId = null;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        accountRepository = new AccountRepository(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<Double> getTotalBalance() { return totalBalance; }
    public LiveData<List<DailyTransactionGroup>> getGroupedTransactions() { return groupedTransactionsLiveData; }
    public LiveData<Transaction> getSelectedTransaction() { return selectedTransaction; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }

    public void setTimeRangeAndReload(Date start, Date end) {
        this.currentStartDate = start;
        this.currentEndDate = end;
        reloadTransactions();
    }

    public void setTypeAndReload(CategoryType type) {
        this.currentType = type;
        reloadTransactions();
    }

    public void reloadTransactions() {
        if (currentStartDate == null || currentEndDate == null) return;
        loadTransactions(currentStartDate, currentEndDate, currentType, currentAccountId, currentCategoryId);
    }
    public void loadTransactions(Date start, Date end, CategoryType type, String accountId, String categoryId) {
        isLoading.setValue(true);
        repository.getFilteredTransactions(start, end, type, accountId, categoryId, new TransactionRepository.TransactionCallback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> result) {
                transactionsLiveData.postValue(result);
                groupedTransactionsLiveData.postValue(groupTransactionsByDate(result));
                isLoading.postValue(false);
            }
            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void loadTransactionById(String id) {
        isLoading.setValue(true);
        repository.getTransactionById(id, new TransactionRepository.TransactionCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                selectedTransaction.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void addTransaction(Transaction transaction) {
        repository.createTransaction(transaction, new TransactionRepository.TransactionCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                operationSuccess.postValue(true);
                String todayString = DateConverter.convertDateToString(new Date());
                CheckInRequest request = new CheckInRequest(todayString);

                userRepository.checkIn(request, new UserRepository.UserCallback<CheckInResponse>() {
                    @Override
                    public void onSuccess(CheckInResponse response) {
                        if (response.isIncreased() && response.getMessage() != null) {
                            checkInMessageLiveData.postValue(response.getMessage());
                        }
                    }

                    @Override
                    public void onError(String message) {
                    }
                });
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void deleteTransaction(String id) {
        repository.deleteTransaction(id, new TransactionRepository.TransactionCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                operationSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void updateTransaction(Transaction transaction) {
        isLoading.setValue(true);
        repository.updateTransaction(transaction, new TransactionRepository.TransactionCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                operationSuccess.postValue(true);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private List<DailyTransactionGroup> groupTransactionsByDate(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) return new ArrayList<>();

        Map<String, List<Transaction>> groupedMap = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            String dateKey = formatToDisplayDate(t.getDate());
            if (!groupedMap.containsKey(dateKey)) {
                groupedMap.put(dateKey, new ArrayList<>());
            }
            groupedMap.get(dateKey).add(t);
        }

        List<DailyTransactionGroup> resultList = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : groupedMap.entrySet()) {
            double totalDay = 0;
            for (Transaction t : entry.getValue()) {
                if (t.getBaseAmount() != null) {
                    if (t.getType() == CategoryType.EXPENSE) {
                        totalDay -= t.getBaseAmount();
                    } else {
                        totalDay += t.getBaseAmount();
                    }
                }
            }
            String dateSummary = String.format(Locale.getDefault(), "%,.0f đ", totalDay);
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

    public void setAccountFilterAndReload(String accountId) {
        this.currentAccountId = accountId;
        reloadTransactions();
    }

    public void setCategoryFilterAndReload(String categoryId) {
        this.currentCategoryId = categoryId;
        reloadTransactions();
    }

    public String getCurrentAccountId() { return currentAccountId; }
    public String getCurrentCategoryId() { return currentCategoryId; }
}
