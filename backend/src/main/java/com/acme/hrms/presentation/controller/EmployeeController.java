package com.acme.hrms.presentation.controller;

import com.acme.hrms.application.dto.*;
import com.acme.hrms.application.service.EmployeeService;
import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Department;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<EmployeeSummaryResponse>> getEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Country country,
            @RequestParam(required = false) Department department,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "employeeCode") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        PagedResponse<EmployeeSummaryResponse> response = employeeService.searchEmployees(
                search, country, department, minSalary, maxSalary, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(employeeService.createEmployee(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployeeProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeProfileRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployeeProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/salary")
    public ResponseEntity<EmployeeResponse> updateSalary(
            @PathVariable UUID id,
            @Valid @RequestBody SalaryUpdateRequest request) {
        return ResponseEntity.ok(employeeService.updateSalary(id, request));
    }

    @GetMapping("/{id}/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getEmployeeAuditLogs(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getAuditLogsForEmployee(id));
    }

    @GetMapping("/recent-audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getRecentAuditLogs() {
        return ResponseEntity.ok(employeeService.getRecentAuditLogs());
    }
}

