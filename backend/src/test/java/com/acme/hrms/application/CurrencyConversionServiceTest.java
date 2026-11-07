package com.acme.hrms.application;

import com.acme.hrms.application.service.CurrencyConversionService;
import com.acme.hrms.domain.model.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyConversionServiceTest {

    private final CurrencyConversionService conversionService = new CurrencyConversionService();

    @Test
    @DisplayName("Should convert EUR to USD accurately")
    void shouldConvertEurToUsd() {
        // 10,000 EUR * 1.085000 = 10,850.00 USD
        BigDecimal usd = conversionService.convertToUSD(new BigDecimal("10000.00"), Currency.EUR);
        assertThat(usd).isEqualByComparingTo("10850.00");
    }

    @Test
    @DisplayName("Should convert INR to USD accurately")
    void shouldConvertInrToUsd() {
        // 1,000,000 INR * 0.012000 = 12,000.00 USD
        BigDecimal usd = conversionService.convertToUSD(new BigDecimal("1000000.00"), Currency.INR);
        assertThat(usd).isEqualByComparingTo("12000.00");
    }

    @Test
    @DisplayName("Should return unchanged amount for USD")
    void shouldReturnUnchangedForUsd() {
        BigDecimal usd = conversionService.convertToUSD(new BigDecimal("75000.00"), Currency.USD);
        assertThat(usd).isEqualByComparingTo("75000.00");
    }
}

