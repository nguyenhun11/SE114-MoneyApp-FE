package com.example.moneyapp.data.repository;

import android.content.Context;

import com.example.moneyapp.data.remote.response.QuestResponse;

import java.util.List;

import retrofit2.Call;

public class QuestRepository extends BaseRepository {
    public QuestRepository(Context context) {
        super(context);
    }

    public Call<List<QuestResponse>> getDailyQuests() {
        return apiService.getDailyQuests();
    }

    public Call<Void> claimQuestReward(String id) {
        return apiService.claimQuestReward(id);
    }
}
