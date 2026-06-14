package com.example.moneyapp.view.components;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.moneyapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeSelectorView extends LinearLayout {

    public enum TimeMode { DAY, WEEK, MONTH, YEAR, CUSTOM }

    private TimeMode currentMode = TimeMode.MONTH;
    private Calendar currentCalendar = Calendar.getInstance();
    private Calendar customStartCal = Calendar.getInstance();
    private Calendar customEndCal = Calendar.getInstance();

    private TextView tabDay, tabWeek, tabMonth, tabYear, tabCustom;
    private TextView tvDateRange;
    private ImageView btnPrev, btnNext, btnJumpToday;

    private OnTimeRangeChangeListener listener;
    private float startX;
    private boolean isDragging = false;
    private static final float SWIPE_THRESHOLD = 100f;

    public interface OnTimeRangeChangeListener {
        void onTimeRangeChanged(Date startDate, Date endDate);
    }

    public TimeSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_time_selector, this, true);
        initViews();
        setupListeners();
        setMode(TimeMode.MONTH); // Mặc định là Tháng cho đẹp
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
        tabCustom = findViewById(R.id.tab_custom);

        tvDateRange = findViewById(R.id.tv_date_range);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnJumpToday = findViewById(R.id.btn_jump_today);

        currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
    }

    private void setupListeners() {
        tabDay.setOnClickListener(v -> setMode(TimeMode.DAY));
        tabWeek.setOnClickListener(v -> setMode(TimeMode.WEEK));
        tabMonth.setOnClickListener(v -> setMode(TimeMode.MONTH));
        tabYear.setOnClickListener(v -> setMode(TimeMode.YEAR));
        tabCustom.setOnClickListener(v -> {
            setMode(TimeMode.CUSTOM);
            showCustomDateRangePicker();
        });

        // BẮT SỰ KIỆN NÚT BẤM (CÓ HIỆU ỨNG TRƯỢT MƯỢT MÀ)
        btnPrev.setOnClickListener(v -> animateChangeAndShift(-1));
        btnNext.setOnClickListener(v -> animateChangeAndShift(1));
        btnJumpToday.setOnClickListener(v -> {
            // Khi nhảy về hiện tại, chữ sẽ lướt từ phải sang trái (tiến về tương lai)
            animateChangeAndShiftToPresent();
        });

        GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (currentMode == TimeMode.CUSTOM) {
                    showCustomDateRangePicker();
                    return true;
                }
                DatePickerDialog picker = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                    currentCalendar.set(year, month, dayOfMonth);
                    updateUIAndNotify();
                }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));

                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                picker.show();
                return true;
            }
        });

        // ÁP DỤNG CẢM BIẾN (Fix cảnh báo Accessibility bằng cách gọi performClick)
        tvDateRange.setClickable(true);
        tvDateRange.setFocusable(true);
        tvDateRange.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);

            if (currentMode == TimeMode.CUSTOM) return true;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    isDragging = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!isDragging) break;
                    float deltaX = event.getRawX() - startX;
                    if (deltaX < 0 && isPresent()) {
                        deltaX = deltaX / 4;
                    }
                    tvDateRange.setTranslationX(deltaX);
                    tvDateRange.setAlpha(1f - Math.min(Math.abs(deltaX) / 400f, 0.7f));
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (!isDragging) break;
                    isDragging = false;
                    float finalDeltaX = event.getRawX() - startX;

                    // Xử lý vuốt chạm hoàn tất (gọi performClick để triệt tiêu cảnh báo)
                    v.performClick();

                    if (finalDeltaX > SWIPE_THRESHOLD) {
                        animateChangeAndShift(-1);
                    } else if (finalDeltaX < -SWIPE_THRESHOLD && !isPresent()) {
                        animateChangeAndShift(1);
                    } else {
                        tvDateRange.animate().translationX(0f).alpha(1f).setDuration(200).start();
                    }
                    break;
            }
            return true;
        });
    }

    private void setMode(TimeMode mode) {
        this.currentMode = mode;
        if (mode != TimeMode.CUSTOM) {
            currentCalendar = Calendar.getInstance();
            currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        }
        updateTabStyles();
        if (mode != TimeMode.CUSTOM) {
            updateUIAndNotify();
        }
    }

    private void updateTabStyles() {
        TextView[] tabs = {tabDay, tabWeek, tabMonth, tabYear, tabCustom};
        TimeMode[] modes = {TimeMode.DAY, TimeMode.WEEK, TimeMode.MONTH, TimeMode.YEAR, TimeMode.CUSTOM};

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

    private void animateChangeAndShift(int direction) {
        float screenOut = direction < 0 ? 300f : -300f;
        float screenIn = direction < 0 ? -300f : 300f;

        tvDateRange.animate()
                .translationX(screenOut)
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> {
                    shiftTimeInternal(direction);
                    updateUIOnly();
                    tvDateRange.setTranslationX(screenIn);
                    tvDateRange.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(200)
                            .withEndAction(this::notifyListener) // Chữ nằm im rồi mới báo API gọi data
                            .start();
                })
                .start();
    }

    private void animateChangeAndShiftToPresent() {
        tvDateRange.animate()
                .translationX(-300f) // Trượt sang trái (tiến về tương lai)
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> {
                    currentCalendar = Calendar.getInstance();
                    currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                    updateUIOnly();

                    tvDateRange.setTranslationX(300f);
                    tvDateRange.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(200)
                            .withEndAction(this::notifyListener)
                            .start();
                })
                .start();
    }

    private void shiftTimeInternal(int amount) {
        switch (currentMode) {
            case DAY:   currentCalendar.add(Calendar.DAY_OF_YEAR, amount); break;
            case WEEK:  currentCalendar.add(Calendar.WEEK_OF_YEAR, amount); break;
            case MONTH: currentCalendar.add(Calendar.MONTH, amount); break;
            case YEAR:  currentCalendar.add(Calendar.YEAR, amount); break;
            case CUSTOM: return;
        }
    }

    private void updateUIOnly() {
        boolean present = isPresent();
        int nextVis = (currentMode == TimeMode.CUSTOM || present) ? View.INVISIBLE : View.VISIBLE;
        btnNext.setVisibility(nextVis);
        btnJumpToday.setVisibility(nextVis);
        btnPrev.setVisibility(currentMode == TimeMode.CUSTOM ? View.INVISIBLE : View.VISIBLE);

        Calendar startCal = (Calendar) currentCalendar.clone();
        Calendar endCal = (Calendar) currentCalendar.clone();
        String displayText = "";

        SimpleDateFormat sdfDay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfWeek = new SimpleDateFormat("dd/MM", Locale.getDefault());

        switch (currentMode) {
            case DAY:
                setStartOfDay(startCal); setEndOfDay(endCal);
                Calendar today = Calendar.getInstance();
                if (startCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) && startCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    displayText = "Hôm nay";
                } else {
                    displayText = sdfDay.format(startCal.getTime());
                }
                break;
            case WEEK:
                startCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); setStartOfDay(startCal);
                endCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); setEndOfDay(endCal);
                displayText = sdfWeek.format(startCal.getTime()) + " - " + sdfDay.format(endCal.getTime());
                break;
            case MONTH:
                startCal.set(Calendar.DAY_OF_MONTH, 1); setStartOfDay(startCal);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH)); setEndOfDay(endCal);
                displayText = "Tháng " + (startCal.get(Calendar.MONTH) + 1) + " - " + startCal.get(Calendar.YEAR);
                break;
            case YEAR:
                startCal.set(Calendar.DAY_OF_YEAR, 1); setStartOfDay(startCal);
                endCal.set(Calendar.MONTH, Calendar.DECEMBER); endCal.set(Calendar.DAY_OF_MONTH, 31); setEndOfDay(endCal);
                displayText = "Năm " + startCal.get(Calendar.YEAR);
                break;
            case CUSTOM:
                startCal = customStartCal; endCal = customEndCal;
                displayText = sdfDay.format(startCal.getTime()) + " - " + sdfDay.format(endCal.getTime());
                break;
        }
        tvDateRange.setText(displayText);
    }

    public void setPredefinedDateRange(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        long diffInMillis = endDate.getTime() - startDate.getTime();
        long diffInDays = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffInMillis);

        if (diffInDays == 0) {
            setMode(TimeMode.DAY);
        } else if (diffInDays >= 6 && diffInDays <= 7) {
            setMode(TimeMode.WEEK);
        } else if (diffInDays >= 27 && diffInDays <= 30) {
            setMode(TimeMode.MONTH);
        } else if (diffInDays >= 364) {
            setMode(TimeMode.YEAR);
        } else {
            setMode(TimeMode.CUSTOM);
            customStartCal = startCal;
            customEndCal = endCal;
        }

        this.currentCalendar = startCal;
        updateUIOnly();
    }

    // Hàm báo cáo API
    private void notifyListener() {
        if (listener == null) return;

        Calendar startCal = (Calendar) currentCalendar.clone();
        Calendar endCal = (Calendar) currentCalendar.clone();

        switch (currentMode) {
            case DAY: setStartOfDay(startCal); setEndOfDay(endCal); break;
            case WEEK:
                startCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); setStartOfDay(startCal);
                endCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); setEndOfDay(endCal); break;
            case MONTH:
                startCal.set(Calendar.DAY_OF_MONTH, 1); setStartOfDay(startCal);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH)); setEndOfDay(endCal); break;
            case YEAR:
                startCal.set(Calendar.DAY_OF_YEAR, 1); setStartOfDay(startCal);
                endCal.set(Calendar.MONTH, Calendar.DECEMBER); endCal.set(Calendar.DAY_OF_MONTH, 31); setEndOfDay(endCal); break;
            case CUSTOM:
                startCal = customStartCal; endCal = customEndCal; break;
        }
        listener.onTimeRangeChanged(startCal.getTime(), endCal.getTime());
    }

    // Giữ lại hàm cũ để gọi trực tiếp (Không animation)
    private void updateUIAndNotify() {
        updateUIOnly();
        notifyListener();
    }

    private void setStartOfDay(Calendar cal) { cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0); }
    private void setEndOfDay(Calendar cal) { cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999); }

    private void showCustomDateRangePicker() {
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            customStartCal.set(year, month, dayOfMonth); setStartOfDay(customStartCal);
            new DatePickerDialog(getContext(), (view2, year2, month2, dayOfMonth2) -> {
                customEndCal.set(year2, month2, dayOfMonth2); setEndOfDay(customEndCal);
                updateUIAndNotify();
            }, year, month, dayOfMonth).show();
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean isPresent() {
        Calendar today = Calendar.getInstance();
        switch (currentMode) {
            case DAY: return currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && currentCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
            case WEEK: return currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && currentCalendar.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR);
            case MONTH: return currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH);
            case YEAR: return currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
            case CUSTOM: return true;
        }
        return false;
    }
}