package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.local.entity.Account;
import com.example.moneyapp.data.repository.AccountRepository;

import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    private final AccountRepository repository;
    private final MutableLiveData<List<Account>> accountsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Account> selectedAccount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();

    public AccountViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountRepository(application);
    }

    public LiveData<List<Account>> getAccountsLiveData() {
        return accountsLiveData;
    }

    public LiveData<Account> getSelectedAccount() {
        return selectedAccount;
    }

    public void selectAccount(Account account) {
        selectedAccount.setValue(account);
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public void loadAccounts() {
        repository.getAllAccounts(accountsLiveData::postValue);
    }

    public void addAccount(Account account) {
        repository.insertAccount(account, () -> saveSuccess.postValue(true));
    }
}
