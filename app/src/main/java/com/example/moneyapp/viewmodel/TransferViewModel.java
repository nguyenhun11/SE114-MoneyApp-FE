package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.request.TransferRequest;
import com.example.moneyapp.data.repository.TransferRepository;
import com.example.moneyapp.model.DailyTransferGroup;
import com.example.moneyapp.model.Transfer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransferViewModel extends AndroidViewModel {
    private final TransferRepository repository;
    private final MutableLiveData<List<DailyTransferGroup>> groupedTransfers = new MutableLiveData<>();
    private final MutableLiveData<Transfer> selectedTransfer = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private Date currentStartDate;
    private Date currentEndDate;
    private String currentAccountId = null;

    public TransferViewModel(@NonNull Application application) {
        super(application);
        repository = new TransferRepository(application);
    }

    public LiveData<List<DailyTransferGroup>> getGroupedTransfers() {
        return groupedTransfers;
    }

    public LiveData<Transfer> getSelectedTransfer() {
        return selectedTransfer;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setTimeRangeAndReload(Date start, Date end) {
        this.currentStartDate = start;
        this.currentEndDate = end;
        reloadTransfers();
    }

    public void setAccountFilterAndReload(String accountId) {
        this.currentAccountId = accountId;
        reloadTransfers();
    }

    public void reloadTransfers() {
        if (currentStartDate == null || currentEndDate == null) return;
        loadTransfers(currentStartDate, currentEndDate);
    }

    public void loadTransfers(Date startDate, Date endDate) {
        this.currentStartDate = startDate;
        this.currentEndDate = endDate;
        isLoading.setValue(true);
        repository.getTransfers(startDate, endDate, null, null, new TransferRepository.TransferCallback<List<Transfer>>() {
            @Override
            public void onSuccess(List<Transfer> result) {
                List<Transfer> filteredList = new ArrayList<>();
                if (currentAccountId != null) {
                    for (Transfer t : result) {
                        if (currentAccountId.equals(t.getSourceAccountId()) ||
                                currentAccountId.equals(t.getDestinationAccountId())) {
                            filteredList.add(t);
                        }
                    }
                } else {
                    filteredList.addAll(result);
                }

                // Sắp xếp giảm dần theo thời gian
                filteredList.sort((t1, t2) -> {
                    if (t1.getDate() == null || t2.getDate() == null) return 0;
                    return t2.getDate().compareTo(t1.getDate());
                });

                groupedTransfers.postValue(groupTransfersByDate(filteredList));
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void loadTransferById(String id) {
        isLoading.setValue(true);
        repository.getTransferById(id, new TransferRepository.TransferCallback<Transfer>() {
            @Override
            public void onSuccess(Transfer result) {
                selectedTransfer.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private List<DailyTransferGroup> groupTransfersByDate(List<Transfer> transfers) {
        if (transfers == null || transfers.isEmpty()) return new ArrayList<>();

        Map<String, List<Transfer>> map = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Formatter cho display label
        java.text.DateFormat displayFormatter = java.text.DateFormat.getDateInstance(
                java.text.DateFormat.LONG, Locale.getDefault());

        for (Transfer t : transfers) {
            if (t.getDate() == null) continue;
            String dateKey = sdf.format(t.getDate());
            if (!map.containsKey(dateKey)) {
                map.put(dateKey, new ArrayList<>());
            }
            map.get(dateKey).add(t);
        }

        List<DailyTransferGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<Transfer>> entry : map.entrySet()) {
            try {
                Date date = sdf.parse(entry.getKey());
                String label = displayFormatter.format(date);
                groups.add(new DailyTransferGroup(label, entry.getValue()));
            } catch (Exception e) {
                groups.add(new DailyTransferGroup(entry.getKey(), entry.getValue()));
            }
        }
        return groups;
    }

    public void createTransfer(TransferRequest request) {
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

    public void updateTransfer(String id, TransferRequest request) {
        repository.updateTransfer(id, request, new TransferRepository.TransferCallback<Transfer>() {
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

    public String getCurrentAccountId() {
        return currentAccountId;
    }
}
