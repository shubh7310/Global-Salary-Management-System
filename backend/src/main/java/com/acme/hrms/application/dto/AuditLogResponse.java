package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
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
        Instant modifiedAt
) {
}

