package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.repository.AccountActivityRepository;
import com.example.moneyapp.model.AccountActivityGroup;
import com.example.moneyapp.model.AccountActivityItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AccountActivityViewModel extends AndroidViewModel {
    private final AccountActivityRepository repository;
    private final MutableLiveData<List<AccountActivityGroup>> groupedActivities = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private Date currentStartDate;
    private Date currentEndDate;

    public AccountActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountActivityRepository(application);
    }

    public LiveData<List<AccountActivityGroup>> getGroupedActivities() {
        return groupedActivities;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void setTimeRangeAndReload(Date start, Date end) {
        this.currentStartDate = start;
        this.currentEndDate = end;
        loadHistory();
    }

    public void loadHistory() {
        if (currentStartDate == null || currentEndDate == null) return;

        repository.getHistory(currentStartDate, currentEndDate, new AccountActivityRepository.AccountActivityCallback() {
            @Override
            public void onSuccess(List<AccountActivityItem> items) {
                groupedActivities.postValue(groupActivities(items));
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    private List<AccountActivityGroup> groupActivities(List<AccountActivityItem> items) {
        // Sort descending by date
        Collections.sort(items, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));

        Map<String, List<AccountActivityItem>> groups = new LinkedHashMap<>();
        SimpleDateFormat fmt = new SimpleDateFormat("d 'tháng' M, yyyy", Locale.getDefault());

        for (AccountActivityItem item : items) {
            String label = fmt.format(item.getDate());
            if (!groups.containsKey(label)) {
                groups.put(label, new ArrayList<>());
            }
            groups.get(label).add(item);
        }

        List<AccountActivityGroup> result = new ArrayList<>();
        for (Map.Entry<String, List<AccountActivityItem>> entry : groups.entrySet()) {
            result.add(new AccountActivityGroup(entry.getKey(), entry.getValue()));
        }
        return result;
    }
}
