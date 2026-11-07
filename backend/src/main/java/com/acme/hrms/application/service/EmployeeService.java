package com.acme.hrms.application.service;

import com.acme.hrms.application.dto.*;
import com.acme.hrms.domain.exception.DomainValidationException;
import com.acme.hrms.domain.exception.EmployeeNotFoundException;
import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Department;
import com.acme.hrms.domain.model.SalaryStructure;
import com.acme.hrms.infrastructure.persistence.entity.CompensationAuditJpaEntity;
import com.acme.hrms.infrastructure.persistence.entity.EmployeeJpaEntity;
import com.acme.hrms.infrastructure.persistence.repository.CompensationAuditJpaRepository;
import com.acme.hrms.infrastructure.persistence.repository.EmployeeJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeJpaRepository employeeRepository;
    private final CompensationAuditJpaRepository auditRepository;
    private final CurrencyConversionService currencyService;

    public EmployeeService(EmployeeJpaRepository employeeRepository,
                           CompensationAuditJpaRepository auditRepository,
                           CurrencyConversionService currencyService) {
        this.employeeRepository = employeeRepository;
        this.auditRepository = auditRepository;
        this.currencyService = currencyService;
    }

    @Transactional(readOnly = true)
    public PagedResponse<EmployeeSummaryResponse> searchEmployees(String query,
                                                                  Country country,
                                                                  Department department,
                                                                  BigDecimal minSalary,
                                                                  BigDecimal maxSalary,
                                                                  int page,
                                                                  int size,
                                                                  String sortBy,
                                                                  String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "ASC"),
                sortBy != null && !sortBy.isBlank() ? sortBy : "employeeCode");

        Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
        Page<EmployeeJpaEntity> entityPage = employeeRepository.findByCriteria(query, country, department, minSalary, maxSalary, pageable);

        List<EmployeeSummaryResponse> dtos = entityPage.getContent().stream().map(e ->
                new EmployeeSummaryResponse(
                        e.getId(),
                        e.getEmployeeCode(),
                        e.getFirstName() + " " + e.getLastName(),
                        e.getDepartment(),
                        e.getJobTitle(),
                        e.getCountry(),
                        e.getCurrency(),
                        e.getAnnualGrossLocal(),
                        e.getAnnualNetLocal(),
                        e.getAnnualGrossUSD(),
                        e.getAnnualNetUSD()
                )
        ).toList();

        return new PagedResponse<>(
                dtos,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isFirst(),
                entityPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(UUID id) {
        EmployeeJpaEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return mapToDetailResponse(entity);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByCode(String code) {
        EmployeeJpaEntity entity = employeeRepository.findByEmployeeCode(code)
                .orElseThrow(() -> new EmployeeNotFoundException(code));
        return mapToDetailResponse(entity);
    }

    public EmployeeResponse updateSalary(UUID id, SalaryUpdateRequest request) {
        EmployeeJpaEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        SalaryStructure newSalary = new SalaryStructure(
                request.currency(),
                request.baseSalary(),
                request.housingAllowance(),
                request.transportAllowance(),
                request.performanceBonus(),
                request.statutoryDeduction()
        );

        BigDecimal prevGrossLocal = entity.getAnnualGrossLocal();
        BigDecimal prevGrossUSD = entity.getAnnualGrossUSD();

        BigDecimal newGrossLocal = newSalary.calculateGrossAnnual();
        BigDecimal newNetLocal = newSalary.calculateNetAnnual();
        BigDecimal newGrossUSD = currencyService.convertToUSD(newGrossLocal, request.currency());
        BigDecimal newNetUSD = currencyService.convertToUSD(newNetLocal, request.currency());

        entity.setCurrency(request.currency());
        entity.setBaseSalary(request.baseSalary());
        entity.setHousingAllowance(request.housingAllowance());
        entity.setTransportAllowance(request.transportAllowance());
        entity.setPerformanceBonus(request.performanceBonus());
        entity.setStatutoryDeduction(request.statutoryDeduction());
        entity.setAnnualGrossLocal(newGrossLocal);
        entity.setAnnualNetLocal(newNetLocal);
        entity.setAnnualGrossUSD(newGrossUSD);
        entity.setAnnualNetUSD(newNetUSD);

        EmployeeJpaEntity saved = employeeRepository.save(entity);

        CompensationAuditJpaEntity audit = new CompensationAuditJpaEntity(
                UUID.randomUUID(),
                entity.getId(),
                entity.getEmployeeCode(),
                entity.getFirstName() + " " + entity.getLastName(),
                request.currency(),
                prevGrossLocal,
                newGrossLocal,
                prevGrossUSD,
                newGrossUSD,
                request.reason(),
                request.modifiedBy() != null && !request.modifiedBy().isBlank() ? request.modifiedBy() : "HR Admin",
                Instant.now()
        );
        auditRepository.save(audit);

        return mapToDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsForEmployee(UUID employeeId) {
        return auditRepository.findByEmployeeIdOrderByModifiedAtDesc(employeeId).stream()
                .map(this::mapAuditResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getRecentAuditLogs() {
        return auditRepository.findTop50ByOrderByModifiedAtDesc().stream()
                .map(this::mapAuditResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeJpaEntity> getEmployeesForExport(String query,
                                                        Country country,
                                                        Department department,
                                                        BigDecimal minSalary,
                                                        BigDecimal maxSalary) {
        return employeeRepository.findAllByCriteria(query, country, department, minSalary, maxSalary);
    }

    private EmployeeResponse mapToDetailResponse(EmployeeJpaEntity entity) {
        SalaryStructure salary = new SalaryStructure(
                entity.getCurrency(),
                entity.getBaseSalary(),
                entity.getHousingAllowance(),
                entity.getTransportAllowance(),
                entity.getPerformanceBonus(),
                entity.getStatutoryDeduction()
        );

        SalaryStructureDto salaryDto = new SalaryStructureDto(
                entity.getCurrency(),
                entity.getBaseSalary(),
                entity.getHousingAllowance(),
                entity.getTransportAllowance(),
                entity.getPerformanceBonus(),
                entity.getStatutoryDeduction(),
                entity.getAnnualGrossLocal(),
                entity.getAnnualNetLocal(),
                salary.calculateMonthlyGross(),
                salary.calculateMonthlyNet(),
                entity.getAnnualGrossUSD(),
                entity.getAnnualNetUSD()
        );

        return new EmployeeResponse(
                entity.getId(),
                entity.getEmployeeCode(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getFirstName() + " " + entity.getLastName(),
                entity.getEmail(),
                entity.getDepartment(),
                entity.getDepartment().getDisplayName(),
                entity.getJobTitle(),
                entity.getCountry(),
                entity.getCountry().getDisplayName(),
                entity.getHireDate(),
                salaryDto
        );
    }

    private AuditLogResponse mapAuditResponse(CompensationAuditJpaEntity entity) {
        return new AuditLogResponse(
                entity.getId(),
                entity.getEmployeeId(),
                entity.getEmployeeCode(),
                entity.getEmployeeName(),
                entity.getCurrency(),
                entity.getPreviousGrossLocal(),
                entity.getNewGrossLocal(),
                entity.getPreviousGrossUSD(),
                entity.getNewGrossUSD(),
                entity.getReason(),
                entity.getModifiedBy(),
                entity.getModifiedAt()
        );
    }
}

