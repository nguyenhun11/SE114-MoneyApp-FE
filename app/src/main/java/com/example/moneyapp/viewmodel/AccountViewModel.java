package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.model.Account;

import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    private final AccountRepository repository;
    private final MutableLiveData<List<Account>> accountsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> totalBalanceLiveData = new MutableLiveData<>();
    private final MutableLiveData<Account> selectedAccount = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();

    public AccountViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountRepository(application);
    }

    public LiveData<List<Account>> getAccountsLiveData() {
        return accountsLiveData;
    }

    public LiveData<Double> getTotalBalanceLiveData() {
        return totalBalanceLiveData;
    }

    public LiveData<Account> getSelectedAccount() {
        return selectedAccount;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public void selectAccount(Account account) {
        selectedAccount.setValue(account);
    }

    public void loadAccounts() {
        repository.getAllAccounts(new AccountRepository.AccountCallback<List<Account>>() {
            @Override
            public void onSuccess(List<Account> result) {
                accountsLiveData.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void loadTotalBalance() {
        repository.getTotalBalance(new AccountRepository.AccountCallback<Double>() {
            @Override
            public void onSuccess(Double result) {
                totalBalanceLiveData.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void addAccount(Account account) {
        repository.insertAccount(account, new AccountRepository.AccountCallback<String>() {
            @Override
            public void onSuccess(String result) {
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void updateAccount(Account account) {
        repository.updateAccount(account, new AccountRepository.AccountCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void deleteAccount(String accountId, String mode, String fallbackId) {
        repository.deleteAccount(accountId, mode, fallbackId, new AccountRepository.AccountCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
                loadAccounts(); // Refresh list after delete
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }
}
