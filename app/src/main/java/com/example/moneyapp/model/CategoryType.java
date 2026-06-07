package com.example.moneyapp.model;

public enum CategoryType {
    TRANSFER(-1),
    EXPENSE(0),
    INCOME(1);

    private final int value;

    CategoryType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
