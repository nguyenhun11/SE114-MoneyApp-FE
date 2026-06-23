package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.BadgeResponse;
import com.example.moneyapp.data.repository.BadgeRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BadgeViewModel extends AndroidViewModel {
    private final BadgeRepository repository;
    private final MutableLiveData<List<BadgeResponse>> badges = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public BadgeViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BadgeRepository(application);
    }

    public LiveData<List<BadgeResponse>> getBadges() { return badges; }
    public LiveData<String> getError() { return error; }

    public void fetchBadges() {
        repository.getUserBadges().enqueue(new Callback<List<BadgeResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<BadgeResponse>> call, @NonNull Response<List<BadgeResponse>> response) {
                if (response.isSuccessful()) {
                    badges.setValue(response.body());
                } else {
                    error.setValue("Không thể tải danh sách huy hiệu");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BadgeResponse>> call, @NonNull Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }
}
