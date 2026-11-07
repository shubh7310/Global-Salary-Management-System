package com.acme.hrms.infrastructure.persistence.entity;

import com.acme.hrms.domain.model.Currency;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for compensation change audit trail.
 */
@Entity
@Table(
    name = "compensation_audit_logs",
    indexes = {
        @Index(name = "idx_audit_emp_id", columnList = "employee_id"),
        @Index(name = "idx_audit_time", columnList = "modified_at")
    }
)
public class CompensationAuditJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "employee_code", nullable = false, length = 32)
    private String employeeCode;

    @Column(name = "employee_name", nullable = false, length = 128)
    private String employeeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 8)
    private Currency currency;

    @Column(name = "prev_gross_local", nullable = false, precision = 15, scale = 2)
    private BigDecimal previousGrossLocal;

    @Column(name = "new_gross_local", nullable = false, precision = 15, scale = 2)
    private BigDecimal newGrossLocal;

    @Column(name = "prev_gross_usd", nullable = false, precision = 15, scale = 2)
    private BigDecimal previousGrossUSD;

    @Column(name = "new_gross_usd", nullable = false, precision = 15, scale = 2)
    private BigDecimal newGrossUSD;

    @Column(name = "reason", nullable = false, length = 512)
    private String reason;

    @Column(name = "modified_by", nullable = false, length = 64)
    private String modifiedBy;

    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    public CompensationAuditJpaEntity() {
    }

    public CompensationAuditJpaEntity(UUID id,
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

