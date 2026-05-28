package com.example.moneyapp.utils;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    public static Date convertStringToDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return new Date();
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
