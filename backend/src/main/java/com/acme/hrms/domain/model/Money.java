package com.acme.hrms.domain.model;

import com.acme.hrms.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object representing a monetary amount in a specific currency.
 * Guarantees zero precision loss using Banker's Rounding (HALF_EVEN).
 */
public final class Money implements Comparable<Money> {

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new DomainValidationException("Monetary amount cannot be null");
        }
        if (currency == null) {
            throw new DomainValidationException("Monetary currency cannot be null");
        }
        this.currency = currency;
        this.amount = amount.setScale(currency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money of(long amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money plus(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money minus(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(BigDecimal factor) {
        if (factor == null) {
            throw new DomainValidationException("Multiplication factor cannot be null");
        }
        return new Money(this.amount.multiply(factor), this.currency);
    }

    public Money divide(long divisor) {
        if (divisor == 0) {
            throw new DomainValidationException("Cannot divide money by zero");
        }
        return new Money(this.amount.divide(BigDecimal.valueOf(divisor), this.currency.getDecimalPlaces(), RoundingMode.HALF_EVEN), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isZeroOrPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private void assertSameCurrency(Money other) {
        if (other == null || this.currency != other.currency) {
            throw new DomainValidationException("Cannot operate on different currencies: " + this.currency + " and " + (other != null ? other.currency : "null"));
        }
    }

    @Override
    public int compareTo(Money o) {
        assertSameCurrency(o);
        return this.amount.compareTo(o.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && currency == money.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getSymbol() + amount.toPlainString() + " " + currency.name();
    }
}

