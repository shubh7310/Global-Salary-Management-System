package com.acme.hrms.domain.model;

import com.acme.hrms.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object encapsulating an employee's compensation breakdown.
 * Invariants:
 *  - Amounts cannot be null or negative.
 *  - Statutory deduction cannot exceed gross compensation.
 */
public final class SalaryStructure {

    private final Currency currency;
    private final BigDecimal baseSalary;
    private final BigDecimal housingAllowance;
    private final BigDecimal transportAllowance;
    private final BigDecimal performanceBonus;
    private final BigDecimal statutoryDeduction;

    public SalaryStructure(Currency currency,
                           BigDecimal baseSalary,
                           BigDecimal housingAllowance,
                           BigDecimal transportAllowance,
                           BigDecimal performanceBonus,
                           BigDecimal statutoryDeduction) {
        if (currency == null) {
            throw new DomainValidationException("Currency is mandatory for salary structure");
        }
        this.currency = currency;
        int scale = currency.getDecimalPlaces();

        this.baseSalary = validateNonNegative(baseSalary, "Base salary", scale);
        this.housingAllowance = validateNonNegative(housingAllowance, "Housing allowance", scale);
        this.transportAllowance = validateNonNegative(transportAllowance, "Transport allowance", scale);
        this.performanceBonus = validateNonNegative(performanceBonus, "Performance bonus", scale);
        this.statutoryDeduction = validateNonNegative(statutoryDeduction, "Statutory deduction", scale);

        BigDecimal gross = calculateGrossAnnual();
        if (this.statutoryDeduction.compareTo(gross) > 0) {
            throw new DomainValidationException("Statutory deduction (" + this.statutoryDeduction + ") cannot exceed gross salary (" + gross + ")");
        }
    }

    private static BigDecimal validateNonNegative(BigDecimal amount, String fieldName, int scale) {
        if (amount == null) {
            throw new DomainValidationException(fieldName + " cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException(fieldName + " cannot be negative: " + amount);
        }
        return amount.setScale(scale, RoundingMode.HALF_EVEN);
    }

    /**
     * Gross Annual = Base + Housing + Transport + Bonus
     */
    public BigDecimal calculateGrossAnnual() {
        return baseSalary.add(housingAllowance)
                         .add(transportAllowance)
                         .add(performanceBonus);
    }

    /**
     * Net Annual = Gross Annual - Statutory Deductions
     */
    public BigDecimal calculateNetAnnual() {
        return calculateGrossAnnual().subtract(statutoryDeduction);
    }

    /**
     * Monthly Gross = Gross Annual / 12
     */
    public BigDecimal calculateMonthlyGross() {
        return calculateGrossAnnual().divide(BigDecimal.valueOf(12), currency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
    }

    /**
     * Monthly Net = Net Annual / 12
     */
    public BigDecimal calculateMonthlyNet() {
        return calculateNetAnnual().divide(BigDecimal.valueOf(12), currency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public BigDecimal getHousingAllowance() {
        return housingAllowance;
    }

    public BigDecimal getTransportAllowance() {
        return transportAllowance;
    }

    public BigDecimal getPerformanceBonus() {
        return performanceBonus;
    }

    public BigDecimal getStatutoryDeduction() {
        return statutoryDeduction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryStructure that = (SalaryStructure) o;
        return currency == that.currency &&
                Objects.equals(baseSalary, that.baseSalary) &&
                Objects.equals(housingAllowance, that.housingAllowance) &&
                Objects.equals(transportAllowance, that.transportAllowance) &&
                Objects.equals(performanceBonus, that.performanceBonus) &&
                Objects.equals(statutoryDeduction, that.statutoryDeduction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, baseSalary, housingAllowance, transportAllowance, performanceBonus, statutoryDeduction);
    }

    @Override
    public String toString() {
        return "SalaryStructure{" +
                "currency=" + currency +
                ", gross=" + calculateGrossAnnual() +
                ", net=" + calculateNetAnnual() +
                '}';
    }
}

