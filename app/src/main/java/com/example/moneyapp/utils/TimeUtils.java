package com.example.moneyapp.utils;

import java.util.TimeZone;

public class TimeUtils {
    public static int getCurrentTimeZoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getOffset(System.currentTimeMillis()) / (1000 * 60 * 60);
    }
}
