package com.example.moneyapp.view.quest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.RewardHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.QuestViewModel;

import java.util.ArrayList;

public class QuestFragment extends BaseFragment {

    private QuestViewModel viewModel;
    private RecyclerView rvQuests;
    private QuestAdapter adapter;
    private ProgressBar pbLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHeader(view, "Nhiệm vụ hàng ngày", true);

        rvQuests = view.findViewById(R.id.rvQuests);
        pbLoading = view.findViewById(R.id.pbLoading);

        rvQuests.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QuestAdapter(new ArrayList<>(), quest -> viewModel.claimReward(quest));
        rvQuests.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(QuestViewModel.class);

        viewModel.getQuests().observe(getViewLifecycleOwner(), quests -> {
            if (quests != null) adapter.updateData(quests);
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> 
            pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE)
        );

        viewModel.getClaimRewardSuccess().observe(getViewLifecycleOwner(), rewardMsg -> {
            if (rewardMsg != null) {
                RewardHelper.showBigReward(requireContext(), rewardMsg, "Chúc mừng bạn đã hoàn thành nhiệm vụ!");
                viewModel.resetClaimSuccess();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        viewModel.fetchQuests();
    }

    @Override
    protected boolean shouldShowFAB() { return false; }
    @Override
    protected boolean shouldShowBottomNavigation() { return false; }
}
