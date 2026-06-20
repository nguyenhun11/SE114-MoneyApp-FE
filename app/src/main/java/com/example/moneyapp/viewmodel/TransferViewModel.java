package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.request.TransferRequest;
import com.example.moneyapp.data.repository.AdjustBalanceRepository;
import com.example.moneyapp.data.repository.TransferRepository;
import com.example.moneyapp.model.AdjustBalance;
import com.example.moneyapp.model.DailyTransferGroup;
import com.example.moneyapp.model.HistoryItem;
import com.example.moneyapp.model.Transfer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransferViewModel extends AndroidViewModel {
    private final TransferRepository transferRepository;
    private final AdjustBalanceRepository adjustRepository;
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
        transferRepository = new TransferRepository(application);
        adjustRepository = new AdjustBalanceRepository(application);
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
        loadCombinedHistory(currentStartDate, currentEndDate);
    }

    public void loadCombinedHistory(Date startDate, Date endDate) {
        this.currentStartDate = startDate;
        this.currentEndDate = endDate;
        isLoading.setValue(true);

        // Nơi chứa dữ liệu thô của 2 API
        List<Transfer> tempTransfers = new ArrayList<>();
        List<AdjustBalance> tempAdjusts = new ArrayList<>();

        // Cờ kiểm soát
        final boolean[] callsCompleted = {false, false}; // [0]: Transfer, [1]: Adjust
        final String[] errorMessage = {null};

        // Hàm gộp khi cả 2 cờ đều là TRUE
        Runnable checkAndMerge = () -> {
            if (callsCompleted[0] && callsCompleted[1]) {
                if (errorMessage[0] != null) {
                    errorLiveData.postValue(errorMessage[0]);
                } else {
                    List<HistoryItem> combinedList = new ArrayList<>();

                    // 1. Lọc và thêm Transfer vào danh sách tổng
                    for (Transfer t : tempTransfers) {
                        if (currentAccountId != null) {
                            if (currentAccountId.equals(t.getSourceAccountId()) ||
                                    currentAccountId.equals(t.getDestinationAccountId())) {
                                combinedList.add(new HistoryItem(t));
                            }
                        } else {
                            combinedList.add(new HistoryItem(t));
                        }
                    }

                    // 2. Thêm AdjustBalance vào danh sách tổng
                    for (AdjustBalance a : tempAdjusts) {
                        combinedList.add(new HistoryItem(a));
                    }

                    // 3. Sắp xếp danh sách tổng hợp giảm dần theo thời gian
                    combinedList.sort((i1, i2) -> {
                        if (i1.getDate() == null || i2.getDate() == null) return 0;
                        return i2.getDate().compareTo(i1.getDate());
                    });

                    // 4. Phân nhóm theo ngày và bắn ra UI
                    groupedTransfers.postValue(groupItemsByDate(combinedList));
                }
                isLoading.postValue(false);
            }
        };

        // GỌI API 1: TRANSFER
        transferRepository.getTransfers(startDate, endDate, null, null, new TransferRepository.TransferCallback<List<Transfer>>() {
            @Override
            public void onSuccess(List<Transfer> result) {
                tempTransfers.addAll(result);
                callsCompleted[0] = true;
                checkAndMerge.run();
            }

            @Override
            public void onError(String message) {
                errorMessage[0] = message;
                callsCompleted[0] = true;
                checkAndMerge.run();
            }
        });

        // GỌI API 2: ADJUST BALANCE (ĐÃ SỬA LỖI TÊN INTERFACE Ở ĐÂY)
        adjustRepository.getAdjustBalances(startDate, endDate, currentAccountId, new AdjustBalanceRepository.AdjustBalanceCallback<List<AdjustBalance>>() {
            @Override
            public void onSuccess(List<AdjustBalance> result) {
                tempAdjusts.addAll(result);
                callsCompleted[1] = true;
                checkAndMerge.run();
            }

            @Override
            public void onError(String message) {
                errorMessage[0] = message;
                callsCompleted[1] = true;
                checkAndMerge.run();
            }
        });
    }

    public void loadTransferById(String id) {
        isLoading.setValue(true);
        transferRepository.getTransferById(id, new TransferRepository.TransferCallback<Transfer>() {
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


    private List<DailyTransferGroup> groupItemsByDate(List<HistoryItem> items) {
        if (items == null || items.isEmpty()) return new ArrayList<>();

        Map<String, List<HistoryItem>> map = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        java.text.DateFormat displayFormatter = java.text.DateFormat.getDateInstance(
                java.text.DateFormat.LONG, Locale.getDefault());

        for (HistoryItem item : items) {
            if (item.getDate() == null) continue;
            String dateKey = sdf.format(item.getDate());
            if (!map.containsKey(dateKey)) {
                map.put(dateKey, new ArrayList<>());
            }
            map.get(dateKey).add(item);
        }

        List<DailyTransferGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<HistoryItem>> entry : map.entrySet()) {
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
        transferRepository.createTransfer(request, new TransferRepository.TransferCallback<Transfer>() {
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
        transferRepository.updateTransfer(id, request, new TransferRepository.TransferCallback<Transfer>() {
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
        transferRepository.deleteTransfer(id, new TransferRepository.TransferCallback<Void>() {
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