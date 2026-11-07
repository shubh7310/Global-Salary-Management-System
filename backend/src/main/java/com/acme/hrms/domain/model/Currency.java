package com.acme.hrms.domain.model;

/**
 * Supported currencies for ACME's global workforce.
 */
public enum Currency {
    USD("$", "US Dollar", 2),
    EUR("€", "Euro", 2),
    GBP("£", "British Pound", 2),
    INR("₹", "Indian Rupee", 2),
    JPY("¥", "Japanese Yen", 0),
    CAD("CA$", "Canadian Dollar", 2),
    AUD("A$", "Australian Dollar", 2),
    SGD("S$", "Singapore Dollar", 2),
    CHF("CHF", "Swiss Franc", 2),
    BRL("R$", "Brazilian Real", 2);

    private final String symbol;
    private final String displayName;
    private final int decimalPlaces;

    Currency(String symbol, String displayName, int decimalPlaces) {
        this.symbol = symbol;
        this.displayName = displayName;
        this.decimalPlaces = decimalPlaces;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }
}

