package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Department;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String employeeCode,
        String firstName,
        String lastName,
        String fullName,
        String email,
        Department department,
        String departmentName,
        String jobTitle,
        Country country,
        String countryName,
        LocalDate hireDate,
        SalaryStructureDto salaryStructure
) {
}

