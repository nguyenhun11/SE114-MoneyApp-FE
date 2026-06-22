package com.example.moneyapp.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CurrencyFormatter {
    private static List<String> supportedCurrencies = new ArrayList<>();
    private static Map<String, Double> rates = new HashMap<>();

    public static void setData(List<String> currencies, Map<String, Double> exchangeRates) {
        supportedCurrencies = currencies;
        rates = exchangeRates;
    }
    public static List<String> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    public static double previewConversion(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) return amount;
        if (rates.isEmpty() || !rates.containsKey(fromCurrency) || !rates.containsKey(toCurrency)) return amount;

        double amountInBase = amount / rates.get(fromCurrency.toUpperCase());
        return amountInBase * rates.get(toCurrency.toUpperCase());
    }

    public static String formatVND(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount);
    }
}
