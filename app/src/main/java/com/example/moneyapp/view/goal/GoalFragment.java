package com.example.moneyapp.view.goal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.GoalViewModel;

public class GoalFragment extends BaseFragment {

    private GoalViewModel goalViewModel;
    private GoalAdapter adapter;
    private ProgressBar loadingBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goalViewModel = new ViewModelProvider(this).get(GoalViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvGoals = view.findViewById(R.id.rv_goals);
        loadingBar = view.findViewById(R.id.loading_bar);

        adapter = new GoalAdapter(goal -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("goal", goal);
            Navigation.findNavController(view).navigate(R.id.goalDetailFragment, bundle);
        });

        rvGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGoals.setAdapter(adapter);

        observeViewModel();
        goalViewModel.fetchGoals();
    }

    @Override
    protected boolean shouldShowFAB() {
        return true;
    }

    @Override
    protected String getFabIcon() {
        return "gmd_add";
    }

    @Override
    protected void onFabClick() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.goalAddFragment);
        }
    }
    
    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    private void observeViewModel() {
        goalViewModel.getGoals().observe(getViewLifecycleOwner(), goals -> {
            adapter.setGoals(goals);
        });

        goalViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        goalViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
