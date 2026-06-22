package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ExchangeRateResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("baseCurrency")
    private String baseCurrency;

    @SerializedName("currencies")
    private List<String> currencies;

    @SerializedName("rates")
    private Map<String, Double> rates;

    // Getters
    public String getMessage() { return message; }
    public String getBaseCurrency() { return baseCurrency; }
    public List<String> getCurrencies() { return currencies; }
    public Map<String, Double> getRates() { return rates; }
}