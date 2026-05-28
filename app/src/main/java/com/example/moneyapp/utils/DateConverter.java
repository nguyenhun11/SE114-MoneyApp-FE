package com.example.moneyapp.utils;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    private static final String API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @TypeConverter
    public static Date convertStringToDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return new Date();
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
            return format.parse(dateStr);
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
}