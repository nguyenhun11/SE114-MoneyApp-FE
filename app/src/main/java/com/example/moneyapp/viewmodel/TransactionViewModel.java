package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.repository.TransactionRepository;
import com.example.moneyapp.model.ListItem;
import com.example.moneyapp.model.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    private final MutableLiveData<List<Transaction>> transactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<ListItem>> groupedTransactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Transaction> selectedTransaction = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
    }

    public LiveData<List<ListItem>> getGroupedTransactions() { return groupedTransactionsLiveData; }
    public LiveData<Transaction> getSelectedTransaction() { return selectedTransaction; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }

    public void loadTransactions(Date start, Date end, Integer type, String accountId, String categoryId) {
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

    private List<ListItem> groupTransactionsByDate(List<Transaction> transactions) {
        if (transactions == null) return new ArrayList<>();
        LinkedHashMap<String, List<Transaction>> map = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            String dateKey = t.getFormattedDate();
            map.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(t);
        }

        List<ListItem> result = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : map.entrySet()) {
            double total = 0;
            for (Transaction t : entry.getValue()) {
                if (t.getAmount() != null) total += t.getAmount();
            }
            result.add(new ListItem(entry.getKey(), String.format(Locale.getDefault(), "%,.0f đ", total)));
            for (Transaction t : entry.getValue()) {
                result.add(new ListItem(t));
            }
        }
        return result;
    }
}
