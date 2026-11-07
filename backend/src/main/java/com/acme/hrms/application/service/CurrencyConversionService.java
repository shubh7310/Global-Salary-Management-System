package com.acme.hrms.application.service;

import com.acme.hrms.domain.exception.DomainValidationException;
import com.acme.hrms.domain.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Service managing foreign exchange conversions to and from the corporate reporting currency (USD).
 * Uses ACME's fixed annual fiscal budget exchange rates.
 */
@Service
public class CurrencyConversionService {

    private final Map<Currency, BigDecimal> ratesToUSD;

    public CurrencyConversionService() {
        Map<Currency, BigDecimal> rates = new EnumMap<>(Currency.class);
        // 1 Local Unit = X USD
        rates.put(Currency.USD, new BigDecimal("1.000000"));
        rates.put(Currency.EUR, new BigDecimal("1.085000"));
        rates.put(Currency.GBP, new BigDecimal("1.282000"));
        rates.put(Currency.CHF, new BigDecimal("1.134000"));
        rates.put(Currency.CAD, new BigDecimal("0.742000"));
        rates.put(Currency.AUD, new BigDecimal("0.665000"));
        rates.put(Currency.SGD, new BigDecimal("0.758000"));
        rates.put(Currency.INR, new BigDecimal("0.012000"));  // ~83.33 INR per USD
        rates.put(Currency.JPY, new BigDecimal("0.006500"));  // ~153.8 JPY per USD
        rates.put(Currency.BRL, new BigDecimal("0.181000"));  // ~5.52 BRL per USD
        this.ratesToUSD = Collections.unmodifiableMap(rates);
    }

    /**
     * Converts a local currency amount to USD using Banker's Rounding.
     */
    public BigDecimal convertToUSD(BigDecimal localAmount, Currency sourceCurrency) {
        if (localAmount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }
        if (sourceCurrency == null || sourceCurrency == Currency.USD) {
            return localAmount.setScale(2, RoundingMode.HALF_EVEN);
        }
        BigDecimal rate = ratesToUSD.get(sourceCurrency);
        if (rate == null) {
            throw new DomainValidationException("Unsupported currency rate for: " + sourceCurrency);
        }
        return localAmount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Converts an amount from USD to a target local currency.
     */
    public BigDecimal convertFromUSD(BigDecimal usdAmount, Currency targetCurrency) {
        if (usdAmount == null) {
            return BigDecimal.ZERO.setScale(targetCurrency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
        }
        if (targetCurrency == null || targetCurrency == Currency.USD) {
            return usdAmount.setScale(2, RoundingMode.HALF_EVEN);
        }
        BigDecimal rate = ratesToUSD.get(targetCurrency);
        if (rate == null) {
            throw new DomainValidationException("Unsupported currency rate for: " + targetCurrency);
        }
        return usdAmount.divide(rate, targetCurrency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
    }

    public BigDecimal getExchangeRateToUSD(Currency currency) {
        return ratesToUSD.getOrDefault(currency, BigDecimal.ONE);
    }

    public Map<Currency, BigDecimal> getAllRatesToUSD() {
        return ratesToUSD;
    }
}

