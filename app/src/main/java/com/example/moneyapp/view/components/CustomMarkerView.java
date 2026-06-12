package com.example.moneyapp.view.components;

import android.content.Context;
import android.widget.TextView;

import com.example.moneyapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.Locale;

public class CustomMarkerView extends MarkerView {

    private TextView tvTitle;
    private TextView tvAmount;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvTitle = findViewById(R.id.tv_marker_title);
        tvAmount = findViewById(R.id.tv_marker_amount);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof BarEntry) {
            BarEntry be = (BarEntry) e;

            // KIỂM TRA: Nếu là Cột Chồng (Tab Thu/Chi)
            if (be.getYVals() != null) {
                int stackIndex = highlight.getStackIndex();
                if (stackIndex >= 0 && stackIndex < be.getYVals().length) {
                    float val = be.getYVals()[stackIndex]; // Lấy giá trị của đúng khối màu đó

                    // Lấy tên Hạng mục từ mảng StackLabels của Chart
                    BarChart chart = (BarChart) getChartView();
                    BarData barData = chart.getBarData();
                    String label = "Chi tiết";

                    if (barData != null && barData.getDataSetCount() > 0) {
                        String[] labels = barData.getDataSetByIndex(0).getStackLabels();
                        if (labels != null && stackIndex < labels.length) {
                            label = labels[stackIndex];
                        }
                    }

                    tvTitle.setText(label);
                    tvAmount.setText(String.format(Locale.getDefault(), "%,.0f đ", val));
                }
            }
            // NẾU LÀ CỘT ĐƠN GROUP (Tab Dòng Tiền)
            else {
                int dataSetIndex = highlight.getDataSetIndex();
                String title = "";
                if (dataSetIndex == 0) title = "Thu nhập";
                else if (dataSetIndex == 1) title = "Chi tiêu";
                else if (dataSetIndex == 2) title = "Cân bằng";

                tvTitle.setText(title);

                double realValue = e.getY();
                if (e.getData() != null) {
                    realValue = (double) e.getData(); // Đọc số âm đã giấu
                }

                tvAmount.setText(String.format(Locale.getDefault(), "%,.0f đ", realValue));
            }
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 10);
    }
}