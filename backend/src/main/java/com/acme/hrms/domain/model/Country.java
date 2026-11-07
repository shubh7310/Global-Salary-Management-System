package com.acme.hrms.domain.model;

/**
 * Operating countries for ACME Corporation.
 */
public enum Country {
    UNITED_STATES("US", "United States", Currency.USD),
    UNITED_KINGDOM("GB", "United Kingdom", Currency.GBP),
    GERMANY("DE", "Germany", Currency.EUR),
    INDIA("IN", "India", Currency.INR),
    JAPAN("JP", "Japan", Currency.JPY),
    CANADA("CA", "Canada", Currency.CAD),
    AUSTRALIA("AU", "Australia", Currency.AUD),
    SINGAPORE("SG", "Singapore", Currency.SGD),
    SWITZERLAND("CH", "Switzerland", Currency.CHF),
    BRAZIL("BR", "Brazil", Currency.BRL);

    private final String countryCode;
    private final String displayName;
    private final Currency defaultCurrency;

    Country(String countryCode, String displayName, Currency defaultCurrency) {
        this.countryCode = countryCode;
        this.displayName = displayName;
        this.defaultCurrency = defaultCurrency;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }
}

