package com.example.moneyapp.data.repository;

import android.content.Context;

import com.example.moneyapp.data.remote.response.QuestClaimResponse;
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

    public Call<QuestClaimResponse> claimQuestReward(String id) {
        return apiService.claimQuestReward(id);
    }
}
