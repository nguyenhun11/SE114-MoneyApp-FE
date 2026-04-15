package com.example.moneyapp.data.repository;

import android.app.Application;

import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.AccountDao;
import com.example.moneyapp.data.local.entity.Account;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountRepository {
    private final AccountDao accountDao;
    private final ExecutorService executor;

    public AccountRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        this.accountDao = database.accountDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void getAllAccounts(OnLoadedListener<List<Account>> listener) {
        executor.execute(() -> {
            List<Account> accounts = accountDao.getAllAccounts();
            if (listener != null) listener.onLoaded(accounts);
        });
    }

    public void insertAccount(Account account, Runnable onComplete) {
        executor.execute(() -> {
            accountDao.insertAccount(account);
            if (onComplete != null) onComplete.run();
        });
    }

    public interface OnLoadedListener<T> {
        void onLoaded(T data);
    }
}
