package com.acme.hrms.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable audit log record for salary adjustments.
 */
public final class CompensationAuditLog {

    private final UUID id;
    private final UUID employeeId;
    private final String employeeCode;
    private final String employeeName;
    private final Currency currency;
    private final BigDecimal previousGrossLocal;
    private final BigDecimal newGrossLocal;
    private final BigDecimal previousGrossUSD;
    private final BigDecimal newGrossUSD;
    private final String reason;
    private final String modifiedBy;
    private final Instant modifiedAt;

    public CompensationAuditLog(UUID id,
                                UUID employeeId,
                                String employeeCode,
                                String employeeName,
                                Currency currency,
                                BigDecimal previousGrossLocal,
                                BigDecimal newGrossLocal,
                                BigDecimal previousGrossUSD,
                                BigDecimal newGrossUSD,
                                String reason,
                                String modifiedBy,
                                Instant modifiedAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.currency = currency;
        this.previousGrossLocal = previousGrossLocal;
        this.newGrossLocal = newGrossLocal;
        this.previousGrossUSD = previousGrossUSD;
        this.newGrossUSD = newGrossUSD;
        this.reason = reason;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt != null ? modifiedAt : Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getPreviousGrossLocal() {
        return previousGrossLocal;
    }

    public BigDecimal getNewGrossLocal() {
        return newGrossLocal;
    }

    public BigDecimal getPreviousGrossUSD() {
        return previousGrossUSD;
    }

    public BigDecimal getNewGrossUSD() {
        return newGrossUSD;
    }

    public String getReason() {
        return reason;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }
}

