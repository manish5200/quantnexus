package com.quantnexus.domain.enums;

import lombok.Getter;

@Getter
public enum TransactionCategory {
    // Income Categories
    SALARY("Monthly salary or wages"),
    INVESTMENT("Dividends, stocks, or interest"),
    GIFTS("Monetary gifts or bonuses"),

    // Expense Categories
    FOOD_AND_DINING("Restaurants, groceries, and cafes"),
    SHOPPING("Clothing, electronics, and personal items"),
    TRANSPORT("Fuel, public transport, or ride-sharing"),
    BILLS_AND_UTILITIES("Electricity, water, internet, or rent"),
    ENTERTAINMENT("Movies, gaming, or hobbies"),
    HEALTHCARE("Medicine, gym, or doctor visits"),
    EDUCATION("Tuition, books, or courses"),
    OTHERS("Miscellaneous transactions");

    private final String description;

    TransactionCategory(String description) {
        this.description = description;
    }
}