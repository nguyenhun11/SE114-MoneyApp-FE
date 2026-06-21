package com.example.moneyapp.utils;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateConverter {

    private static final String API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String API_DATE_FORMAT_WITH_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @TypeConverter
    public static Date convertStringToDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return new Date();
        }

        // Loại bỏ phần nano giây thừa (nếu có) vì SimpleDateFormat chỉ hỗ trợ tới mili giây (.SSS)
        // Ví dụ: 2026-06-07T14:39:22.2457911 -> 2026-06-07T14:39:22.245
        String cleanDateStr = dateStr;
        if (dateStr.contains(".")) {
            int dotIndex = dateStr.indexOf(".");
            int endIndex = Math.min(dotIndex + 4, dateStr.length());
            cleanDateStr = dateStr.substring(0, endIndex);
        }

        try {
            String formatPattern = cleanDateStr.contains(".") ? API_DATE_FORMAT_WITH_MILLIS : API_DATE_FORMAT;
            SimpleDateFormat format = new SimpleDateFormat(formatPattern, Locale.getDefault());
            // Đảm bảo parse đúng múi giờ UTC nếu server gửi về dạng Z
            if (dateStr.endsWith("Z")) {
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
            }
            return format.parse(cleanDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    @TypeConverter
    public static String convertDateToString(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
        return format.format(date);
    }

    public static String formatToDisplay(String apiDateString) {
        if (apiDateString == null || apiDateString.isEmpty()) return "";
        try {
            // Cắt bớt phần giờ nếu là yyyy-MM-dd
            String dateOnly = apiDateString.split("T")[0];
            SimpleDateFormat apiFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = apiFmt.parse(dateOnly);
            if (date == null) return apiDateString;
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
        } catch (Exception e) {
            return apiDateString;
        }
    }
}
