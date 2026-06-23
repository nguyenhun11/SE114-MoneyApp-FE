package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.QuestResponse;
import com.example.moneyapp.data.repository.QuestRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestViewModel extends AndroidViewModel {
    private final QuestRepository repository;
    private final MutableLiveData<List<QuestResponse>> quests = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> claimRewardSuccess = new MutableLiveData<>();

    public QuestViewModel(@NonNull Application application) {
        super(application);
        this.repository = new QuestRepository(application);
    }

    public LiveData<List<QuestResponse>> getQuests() { return quests; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getClaimRewardSuccess() { return claimRewardSuccess; }

    public void fetchQuests() {
        loading.setValue(true);
        repository.getDailyQuests().enqueue(new Callback<List<QuestResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<QuestResponse>> call, @NonNull Response<List<QuestResponse>> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    quests.setValue(response.body());
                } else {
                    error.setValue("Không thể tải danh sách nhiệm vụ");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<QuestResponse>> call, @NonNull Throwable t) {
                loading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void claimReward(QuestResponse quest) {
        repository.claimQuestReward(quest.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    String rewardMsg = "+" + quest.getRewardPoints() + (quest.getRewardType() == 1 ? " PP" : " SP");
                    claimRewardSuccess.setValue(rewardMsg);
                    fetchQuests();
                } else {
                    error.setValue("Lỗi khi nhận thưởng");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }
    
    public void resetClaimSuccess() {
        claimRewardSuccess.setValue(null);
    }
}
