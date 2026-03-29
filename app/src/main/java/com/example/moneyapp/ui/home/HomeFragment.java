package com.example.moneyapp.ui.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;

public class HomeFragment extends BaseFragment {

    private TextView tvExpense, tvIncome;
    private View animatedIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvExpense = view.findViewById(R.id.tv_tab_expense);
        tvIncome = view.findViewById(R.id.tv_tab_income);
        animatedIndicator = view.findViewById(R.id.animated_indicator);

        tvExpense.setOnClickListener(v -> switchTab(true));
        tvIncome.setOnClickListener(v -> switchTab(false));
    }

    private void switchTab(boolean isExpense) {
        int colorSelected = ContextCompat.getColor(requireContext(), R.color.tabSelectedColor);
        int colorUnselected = ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant);

        if (isExpense) {
            tvExpense.setTextColor(colorSelected);
            tvExpense.setTypeface(null, Typeface.BOLD);
            tvIncome.setTextColor(colorUnselected);
            tvIncome.setTypeface(null, Typeface.NORMAL);

            animatedIndicator.animate()
                    .translationX(0)
                    .setDuration(250)
                    .start();
        } else {
            tvIncome.setTextColor(colorSelected);
            tvIncome.setTypeface(null, Typeface.BOLD);
            tvExpense.setTextColor(colorUnselected);
            tvExpense.setTypeface(null, Typeface.NORMAL);

            float distance = tvIncome.getX() - tvExpense.getX();

            animatedIndicator.animate()
                    .translationX(distance)
                    .setDuration(250)
                    .start();
        }
    }
}
