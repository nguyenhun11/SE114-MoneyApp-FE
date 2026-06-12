package com.example.moneyapp.view.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.moneyapp.R;

import java.util.Calendar;
import java.util.Date;

public class StatisticTimeSelectorView extends LinearLayout {

    public enum TimeMode { DAY, WEEK, MONTH, YEAR }

    private TimeMode currentMode = TimeMode.MONTH;
    private TextView tabDay, tabWeek, tabMonth, tabYear;

    private OnTimeRangeChangeListener listener;

    public interface OnTimeRangeChangeListener {
        void onTimeRangeChanged(Date startDate, Date endDate, int groupBy);
    }

    public StatisticTimeSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_statistic_time_selector, this, true);
        initViews();
        setupListeners();
        setMode(TimeMode.MONTH); // Mặc định là Tháng
    }

    public void setOnTimeRangeChangeListener(OnTimeRangeChangeListener listener) {
        this.listener = listener;
        updateUIAndNotify();
    }

    private void initViews() {
        tabDay = findViewById(R.id.tab_day);
        tabWeek = findViewById(R.id.tab_week);
        tabMonth = findViewById(R.id.tab_month);
        tabYear = findViewById(R.id.tab_year);
    }

    private void setupListeners() {
        tabDay.setOnClickListener(v -> setMode(TimeMode.DAY));
        tabWeek.setOnClickListener(v -> setMode(TimeMode.WEEK));
        tabMonth.setOnClickListener(v -> setMode(TimeMode.MONTH));
        tabYear.setOnClickListener(v -> setMode(TimeMode.YEAR));
    }

    private void setMode(TimeMode mode) {
        if (this.currentMode == mode) return;
        this.currentMode = mode;
        updateTabStyles();
        updateUIAndNotify();
    }

    private void updateTabStyles() {
        TextView[] tabs = {tabDay, tabWeek, tabMonth, tabYear};
        TimeMode[] modes = {TimeMode.DAY, TimeMode.WEEK, TimeMode.MONTH, TimeMode.YEAR};

        for (int i = 0; i < tabs.length; i++) {
            if (currentMode == modes[i]) {
                tabs[i].setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                tabs[i].setTypeface(null, Typeface.BOLD);
            } else {
                tabs[i].setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                tabs[i].setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private void updateUIAndNotify() {
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);

        int groupBy = 0;
        switch (currentMode) {
            case DAY: groupBy = 0; break;
            case WEEK: groupBy = 1; break;
            case MONTH: groupBy = 2; break;
            case YEAR: groupBy = 3; break;
        }

        if (listener != null) {
            // Truyền cứng startDate = null để API tự load mọi dữ liệu
            listener.onTimeRangeChanged(null, endCal.getTime(), groupBy);
        }
    }
}