package com.acme.hrms.application.service;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.Department;
import com.acme.hrms.infrastructure.persistence.entity.EmployeeJpaEntity;
import com.acme.hrms.infrastructure.persistence.repository.EmployeeJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

@Service
public class DataSeedingService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeedingService.class);

    private final EmployeeJpaRepository employeeRepository;
    private final CurrencyConversionService currencyService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.seeding.enabled:true}")
    private boolean seedingEnabled;

    @Value("${app.seeding.target-records:10000}")
    private int targetRecords;

    @Value("${app.seeding.batch-size:500}")
    private int batchSize;

    private static final String[] FIRST_NAMES = {
            "Alexander", "Amara", "Carlos", "Chen", "Chloe", "David", "Elena", "Fatima",
            "Gabriel", "Hanna", "Hiroshi", "Isabella", "James", "Jia", "Liam", "Mateo",
            "Maya", "Mohammed", "Neha", "Noah", "Olivia", "Priya", "Rahul", "Sarah",
            "Shubham", "Sophia", "Takeshi", "Thomas", "Vikram", "Yuki", "Zara", "Zoe"
    };

    private static final String[] LAST_NAMES = {
            "Anderson", "Chen", "Dubois", "Fernandez", "Garcia", "Ito", "Johnson", "Kim",
            "Kumar", "Muller", "Nakamura", "Patel", "Rossi", "Santos", "Schmidt", "Sharma",
            "Silva", "Singh", "Smith", "Suzuki", "Tanaka", "Taylor", "Venkatesh", "Wang",
            "Williams", "Wong", "Yamamoto", "Zhang"
    };

    private static final String[][] TITLES_BY_DEPT = {
            {"Junior Software Engineer", "Software Engineer", "Senior Software Craftsperson", "Staff Engineer", "Principal Architect", "Engineering Director"},
            {"Associate Product Manager", "Product Manager", "Senior Product Manager", "Lead Product Manager", "VP of Product"},
            {"Sales Development Rep", "Account Executive", "Senior Account Executive", "Enterprise Sales Director", "VP of Sales"},
            {"Growth Marketing Specialist", "Content Strategist", "Senior Brand Manager", "Marketing Lead", "VP of Marketing"},
            {"HR Generalist", "People Partner", "Senior Talent Acquisition Specialist", "Total Rewards Lead", "Head of People"},
            {"Financial Analyst", "Senior Accountant", "Finance Controller", "Head of FP&A", "Finance Director"},
            {"Operations Coordinator", "Technical Support Specialist", "Operations Manager", "Director of Business Operations"},
            {"Legal Counsel", "Senior Compliance Officer", "Corporate Counsel", "General Counsel"}
    };

    // Base salary ranges in USD for seniority levels (0=Junior to 5=Director)
    private static final int[][] BASE_SALARY_BANDS_USD = {
            {55000, 75000},    // Level 0: Entry/Junior
            {75000, 105000},   // Level 1: Mid
            {105000, 145000},  // Level 2: Senior
            {145000, 185000},  // Level 3: Lead/Staff
            {185000, 230000},  // Level 4: Principal/Head
            {230000, 310000}   // Level 5: Director/VP
    };

    public DataSeedingService(EmployeeJpaRepository employeeRepository,
                              CurrencyConversionService currencyService) {
        this.employeeRepository = employeeRepository;
        this.currencyService = currencyService;
    }

    @Override
    public void run(String... args) {
        if (!seedingEnabled) {
            log.info("Data seeding is disabled in configuration.");
            return;
        }

        long count = employeeRepository.count();
        if (count >= targetRecords) {
            log.info("Database already seeded with {} records. Skipping initialization.", count);
            return;
        }

        log.info("Starting batch seeding of {} employee records...", targetRecords);
        long startTime = System.currentTimeMillis();
        seedData(targetRecords);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Successfully seeded {} employees in {} ms.", targetRecords, duration);
    }

    @Transactional
    public void seedData(int count) {
        Random rng = new Random(42); // Deterministic seed for reproducible testing
        Country[] countries = Country.values();
        Department[] departments = Department.values();

        for (int i = 1; i <= count; i++) {
            String code = String.format("EMP-%05d", i);
            String first = FIRST_NAMES[rng.nextInt(FIRST_NAMES.length)];
            String last = LAST_NAMES[rng.nextInt(LAST_NAMES.length)];
            String email = (first + "." + last + i + "@acme.com").toLowerCase();

            Department dept = departments[rng.nextInt(departments.length)];
            Country country = countries[rng.nextInt(countries.length)];
            Currency currency = country.getDefaultCurrency();

            String[] titles = TITLES_BY_DEPT[dept.ordinal()];
            int level = rng.nextInt(titles.length);
            String title = titles[level];

            int bandIdx = Math.min(level, BASE_SALARY_BANDS_USD.length - 1);
            int minUSD = BASE_SALARY_BANDS_USD[bandIdx][0];
            int maxUSD = BASE_SALARY_BANDS_USD[bandIdx][1];
            int targetUSD = minUSD + rng.nextInt(maxUSD - minUSD + 1);

            BigDecimal targetGrossUSD = BigDecimal.valueOf(targetUSD);
            BigDecimal targetGrossLocal = currencyService.convertFromUSD(targetGrossUSD, currency);

            // Salary Component distribution:
            // Base: 70%, Housing: 12%, Transport: 4%, Bonus: 14%
            BigDecimal base = targetGrossLocal.multiply(new BigDecimal("0.70")).setScale(currency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
            BigDecimal housing = targetGrossLocal.multiply(new BigDecimal("0.12")).setScale(currency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
            BigDecimal transport = targetGrossLocal.multiply(new BigDecimal("0.04")).setScale(currency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
            BigDecimal bonus = targetGrossLocal.subtract(base).subtract(housing).subtract(transport);

            // Statutory Deduction: ~20% of gross
            BigDecimal deduction = targetGrossLocal.multiply(new BigDecimal("0.20")).setScale(currency.getDecimalPlaces(), RoundingMode.HALF_EVEN);

            BigDecimal actualGrossLocal = base.add(housing).add(transport).add(bonus);
            BigDecimal actualNetLocal = actualGrossLocal.subtract(deduction);

            BigDecimal annualGrossUSD = currencyService.convertToUSD(actualGrossLocal, currency);
            BigDecimal annualNetUSD = currencyService.convertToUSD(actualNetLocal, currency);

            LocalDate hireDate = LocalDate.of(2018 + rng.nextInt(7), 1 + rng.nextInt(12), 1 + rng.nextInt(28));

            EmployeeJpaEntity entity = new EmployeeJpaEntity(
                    UUID.randomUUID(),
                    code,
                    first,
                    last,
                    email,
                    dept,
                    title,
                    country,
                    hireDate,
                    currency,
                    base,
                    housing,
                    transport,
                    bonus,
                    deduction,
                    actualGrossLocal,
                    actualNetLocal,
                    annualGrossUSD,
                    annualNetUSD
            );

            entityManager.persist(entity);

            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }
}

