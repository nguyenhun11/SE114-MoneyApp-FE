package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.repository.AdjustBalanceRepository;
import com.example.moneyapp.model.AdjustBalance;

public class AdjustBalanceViewModel extends AndroidViewModel {

    private final AdjustBalanceRepository repository;
    private final MutableLiveData<AdjustBalance> selectedAdjustBalance = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AdjustBalanceViewModel(@NonNull Application application) {
        super(application);
        repository = new AdjustBalanceRepository(application);
    }

    public LiveData<AdjustBalance> getSelectedAdjustBalance() {
        return selectedAdjustBalance;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void setAdjustBalanceData(AdjustBalance adjustBalance) {
        if (adjustBalance != null) {
            selectedAdjustBalance.setValue(adjustBalance);
        } else {
            errorLiveData.setValue("Lỗi: Không nhận được dữ liệu điều chỉnh");
        }
    }
}