package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.request.TransactionRequest;
import com.example.moneyapp.data.repository.TransferRepository;
import com.example.moneyapp.model.Transfer;

import java.util.Date;
import java.util.List;

public class TransferViewModel extends AndroidViewModel {
    private final TransferRepository repository;
    private final MutableLiveData<List<Transfer>> transfersLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    public TransferViewModel(@NonNull Application application) {
        super(application);
        repository = new TransferRepository(application);
    }

    public LiveData<List<Transfer>> getTransfersLiveData() {
        return transfersLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    public void loadTransfers(Date start, Date end, String source, String destination) {
        repository.getTransfers(start, end, source, destination, new TransferRepository.TransferCallback<List<Transfer>>() {
            @Override
            public void onSuccess(List<Transfer> result) {
                transfersLiveData.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void createTransfer(TransactionRequest request) {
        repository.createTransfer(request, new TransferRepository.TransferCallback<Transfer>() {
            @Override
            public void onSuccess(Transfer result) {
                operationSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void deleteTransfer(String id) {
        repository.deleteTransfer(id, new TransferRepository.TransferCallback<Void>() {
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
}
