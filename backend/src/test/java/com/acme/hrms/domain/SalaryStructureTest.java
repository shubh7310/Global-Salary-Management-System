package com.acme.hrms.domain;

import com.acme.hrms.domain.exception.DomainValidationException;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.SalaryStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SalaryStructureTest {

    @Test
    @DisplayName("Should correctly calculate gross and net annual and monthly amounts")
    void shouldCalculateGrossAndNetAmounts() {
        SalaryStructure structure = new SalaryStructure(
                Currency.USD,
                new BigDecimal("100000.00"), // base
                new BigDecimal("15000.00"),  // housing
                new BigDecimal("5000.00"),   // transport
                new BigDecimal("20000.00"),  // bonus
                new BigDecimal("25000.00")   // deduction
        );

        // Gross = 100k + 15k + 5k + 20k = 140,000.00
        assertThat(structure.calculateGrossAnnual()).isEqualByComparingTo("140000.00");
        // Net = 140k - 25k = 115,000.00
        assertThat(structure.calculateNetAnnual()).isEqualByComparingTo("115000.00");

        // Monthly Gross = 140,000 / 12 = 11,666.67
        assertThat(structure.calculateMonthlyGross()).isEqualByComparingTo("11666.67");
        // Monthly Net = 115,000 / 12 = 9,583.33
        assertThat(structure.calculateMonthlyNet()).isEqualByComparingTo("9583.33");
    }

    @Test
    @DisplayName("Should reject negative salary components")
    void shouldRejectNegativeComponents() {
        assertThatThrownBy(() -> new SalaryStructure(
                Currency.USD,
                new BigDecimal("-50000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        )).isInstanceOf(DomainValidationException.class)
          .hasMessageContaining("Base salary cannot be negative");
    }

    @Test
    @DisplayName("Should reject statutory deduction exceeding gross salary")
    void shouldRejectExcessiveDeductions() {
        assertThatThrownBy(() -> new SalaryStructure(
                Currency.EUR,
                new BigDecimal("50000.00"),
                new BigDecimal("5000.00"),
                new BigDecimal("2000.00"),
                new BigDecimal("3000.00"),
                new BigDecimal("70000.00") // exceeds 60,000 gross
        )).isInstanceOf(DomainValidationException.class)
          .hasMessageContaining("cannot exceed gross salary");
    }

    @Test
    @DisplayName("Should handle zero bonus and zero allowances safely")
    void shouldHandleZeroValuesSafely() {
        SalaryStructure minimal = new SalaryStructure(
                Currency.GBP,
                new BigDecimal("60000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("12000.00")
        );

        assertThat(minimal.calculateGrossAnnual()).isEqualByComparingTo("60000.00");
        assertThat(minimal.calculateNetAnnual()).isEqualByComparingTo("48000.00");
    }
}

