package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.request.CheckInRequest;
import com.example.moneyapp.data.remote.response.CheckInResponse;
import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.data.repository.CityRepository;
import com.example.moneyapp.data.repository.TransactionRepository;
import com.example.moneyapp.data.repository.UserRepository;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.utils.DateConverter;

import java.util.Date;

public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<Transaction> selectedTransaction = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> checkInMessageLiveData = new MutableLiveData<>();

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<Transaction> getSelectedTransaction() { return selectedTransaction; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }
    public LiveData<String> getCheckInMessageLiveData() { return checkInMessageLiveData; }

    public void loadTransactionById(String id) {
        isLoading.setValue(true);
        transactionRepository.getTransactionById(id, new TransactionRepository.TransactionCallback<Transaction>() {
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
        isLoading.setValue(true);
        transactionRepository.createTransaction(transaction, new TransactionRepository.TransactionCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                operationSuccess.postValue(true);

                // Refresh City data to update Stability points
                new CityRepository(getApplication()).getCity().enqueue(new retrofit2.Callback<com.example.moneyapp.data.remote.response.CityResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.moneyapp.data.remote.response.CityResponse> call, retrofit2.Response<com.example.moneyapp.data.remote.response.CityResponse> response) {}
                    @Override
                    public void onFailure(retrofit2.Call<com.example.moneyapp.data.remote.response.CityResponse> call, Throwable t) {}
                });

                isLoading.postValue(false);

                // Ghi nhận điểm danh (Streak)
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
                    public void onError(String message) { }
                });
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void updateTransaction(Transaction transaction) {
        isLoading.setValue(true);
        transactionRepository.updateTransaction(transaction, new TransactionRepository.TransactionCallback<Transaction>() {
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

    public void deleteTransaction(String id) {
        isLoading.setValue(true);
        transactionRepository.deleteTransaction(id, new TransactionRepository.TransactionCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
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
}