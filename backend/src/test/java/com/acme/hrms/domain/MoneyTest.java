package com.acme.hrms.domain;

import com.acme.hrms.domain.exception.DomainValidationException;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    @DisplayName("Should create Money with banker's rounding and exact scale")
    void shouldCreateMoneyWithBankersRounding() {
        Money money = Money.of(new BigDecimal("100.555"), Currency.USD);
        // Half-even round: 100.56
        assertThat(money.getAmount()).isEqualByComparingTo("100.56");
        assertThat(money.getCurrency()).isEqualTo(Currency.USD);
    }

    @Test
    @DisplayName("Should add money with same currency")
    void shouldAddMoneyWithSameCurrency() {
        Money m1 = Money.of(new BigDecimal("150.00"), Currency.EUR);
        Money m2 = Money.of(new BigDecimal("50.25"), Currency.EUR);

        Money sum = m1.plus(m2);

        assertThat(sum.getAmount()).isEqualByComparingTo("200.25");
        assertThat(sum.getCurrency()).isEqualTo(Currency.EUR);
    }

    @Test
    @DisplayName("Should throw exception when adding different currencies")
    void shouldThrowWhenAddingDifferentCurrencies() {
        Money usd = Money.of(100, Currency.USD);
        Money eur = Money.of(100, Currency.EUR);

        assertThatThrownBy(() -> usd.plus(eur))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Cannot operate on different currencies");
    }

    @Test
    @DisplayName("Should multiply money by factor accurately")
    void shouldMultiplyMoneyByFactor() {
        Money salary = Money.of(new BigDecimal("50000.00"), Currency.GBP);
        Money multiplied = salary.multiply(new BigDecimal("1.10")); // 10% raise

        assertThat(multiplied.getAmount()).isEqualByComparingTo("55000.00");
    }

    @Test
    @DisplayName("Should divide money correctly into monthly intervals")
    void shouldDivideMoneyCorrectly() {
        Money annual = Money.of(new BigDecimal("120000.00"), Currency.USD);
        Money monthly = annual.divide(12);

        assertThat(monthly.getAmount()).isEqualByComparingTo("10000.00");
    }
}

