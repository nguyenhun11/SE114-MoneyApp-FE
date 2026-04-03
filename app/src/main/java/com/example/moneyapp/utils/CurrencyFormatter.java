package com.example.moneyapp.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyFormatter {

    /**
     * Định dạng số thành chuỗi tiền tệ VNĐ (ví dụ: 100000 -> 100.000)
     */
    public static String formatVND(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount);
    }

    /**
     * Thêm đơn vị tiền tệ vào sau số đã định dạng
     */
    public static String formatWithCurrency(double amount, String currencySymbol) {
        return formatVND(amount) + " " + currencySymbol;
    }
}
