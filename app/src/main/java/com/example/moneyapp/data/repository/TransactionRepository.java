package com.example.moneyapp.data.repository;

import android.app.Application;
import android.content.Context;

import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.TransactionDao;
import com.example.moneyapp.data.local.entity.Transaction;
import com.example.moneyapp.data.local.pojo.TransactionWithDetails;
import com.example.moneyapp.utils.PreferenceManager;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {
    private final TransactionDao transactionDao;
    private final ExecutorService executorService;
    private final Context context;
    private String currentUserID;

    public interface TransactionCallback{
        void onSuccess(Transaction transaction);
        void onSuccess(List<Transaction> transactionList);
        void onError(String message);
    }

    public TransactionRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        this.transactionDao = appDatabase.transactionDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.context = application.getApplicationContext();
        this.currentUserID = PreferenceManager.getInstance(context).getUserID();
    }

    public void addTransaction(Transaction transaction, TransactionCallback callback){
        executorService.execute(()->{
            try {
                transactionDao.insertTransaction(transaction);
                callback.onSuccess(transaction);
            }
            catch (Exception e){
                callback.onError("System error: " + e.getMessage());
            }
        });
    }

    public void getTransactionByID(String transactionID, TransactionCallback callback){
        executorService.execute(()-> {
            try{
                Transaction transaction = transactionDao.getTransactionById(transactionID);
                callback.onSuccess(transaction);
            } catch (Exception e) {
                callback.onError("System error" + e.getMessage());
            }
        });
    }

    public void getFilteredTransactions(
            int type,
            Date startDate,
            Date endDate,
            String accountID,  //null if all
            String categoryID, //null if all
            TransactionCallback callback){
        executorService.execute(()->{
           try {
               List<Transaction> transactionList = transactionDao.getAllTransactions(
                       type,
                       startDate,
                       endDate,
                       accountID,
                       categoryID,
                       currentUserID
               );
               callback.onSuccess(transactionList);
           } catch (Exception e) {
               callback.onError("System error" + e.getMessage());
           }
        });
    }
    // Thêm interface + method vào TransactionRepository
    public interface TransactionWithDetailsCallback {
        void onSuccess(List<TransactionWithDetails> list);
        void onError(String message);
    }

    public void getTransactionsWithDetails(Date startDate, Date endDate,
                                           TransactionWithDetailsCallback callback) {
        executorService.execute(() -> {
            try {
                List<TransactionWithDetails> list =
                        transactionDao.getTransactionsWithDetails(startDate, endDate, currentUserID);
                callback.onSuccess(list);
            } catch (Exception e) {
                callback.onError("System error: " + e.getMessage());
            }
        });
    }
}
