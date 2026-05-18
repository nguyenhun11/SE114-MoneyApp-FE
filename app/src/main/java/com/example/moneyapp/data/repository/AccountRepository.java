package com.example.moneyapp.data.repository;

import android.app.Application;
import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.AccountDao;
import com.example.moneyapp.data.local.entity.Account;
import com.example.moneyapp.utils.PreferenceManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountRepository {
    private final AccountDao accountDao;
    private final ExecutorService executor;
    private final String currentUserId;

    public interface AccountCallback {
        void onSuccess(List<Account> accounts);
        void onError(String message);
    }

    public AccountRepository(Application application) {
        accountDao = AppDatabase.getInstance(application).accountDao();
        executor = Executors.newSingleThreadExecutor();
        currentUserId = PreferenceManager.getInstance(application).getUserID();
    }

    public void getAccounts(AccountCallback callback) {
        executor.execute(() -> {
            try {
                List<Account> accounts = accountDao.getAccountsByUserId(currentUserId);
                callback.onSuccess(accounts);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
}