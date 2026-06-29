package com.example.moneyapp.view.city;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CityViewModel;
import com.google.android.material.tabs.TabLayout;

public class CityRankFragment extends BaseFragment {

    private CityViewModel viewModel;
    private CityRankAdapter adapter;
    private RecyclerView rvRanking;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TabLayout tabRanking;
    private int currentType = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_rank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHeader(view, "Bảng xếp hạng", true);

        rvRanking = view.findViewById(R.id.rvRanking);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tabRanking = view.findViewById(R.id.tabRanking);

        adapter = new CityRankAdapter();
        rvRanking.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRanking.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(CityViewModel.class);

        viewModel.getRankingData().observe(getViewLifecycleOwner(), list -> {
            progressBar.setVisibility(View.GONE);
            if (list == null || list.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvRanking.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvRanking.setVisibility(View.VISIBLE);
                adapter.setData(list, currentType);
            }
        });

        tabRanking.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentType = tab.getPosition() + 1;
                loadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadData();
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvRanking.setVisibility(View.GONE);
        viewModel.fetchRankingData(currentType);
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    @Override
    protected boolean shouldShowFAB() {
        return false;
    }
}
