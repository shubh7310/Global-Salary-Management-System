package com.acme.hrms.infrastructure.persistence.mapper;

import com.acme.hrms.domain.model.Employee;
import com.acme.hrms.domain.model.SalaryStructure;
import com.acme.hrms.infrastructure.persistence.entity.EmployeeJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Clean mapper between Domain Model and JPA Infrastructure Entity.
 */
@Component
public class EmployeeEntityMapper {

    public Employee toDomain(EmployeeJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        SalaryStructure salaryStructure = new SalaryStructure(
                entity.getCurrency(),
                entity.getBaseSalary(),
                entity.getHousingAllowance(),
                entity.getTransportAllowance(),
                entity.getPerformanceBonus(),
                entity.getStatutoryDeduction()
        );

        return new Employee(
                entity.getId(),
                entity.getEmployeeCode(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getDepartment(),
                entity.getJobTitle(),
                entity.getCountry(),
                entity.getHireDate(),
                salaryStructure,
                entity.getAnnualGrossUSD(),
                entity.getAnnualNetUSD()
        );
    }

    public EmployeeJpaEntity toJpaEntity(Employee domain) {
        if (domain == null) {
            return null;
        }

        SalaryStructure salary = domain.getSalaryStructure();

        return new EmployeeJpaEntity(
                domain.getId(),
                domain.getEmployeeCode(),
                domain.getFirstName(),
                domain.getLastName(),
                domain.getEmail(),
                domain.getDepartment(),
                domain.getJobTitle(),
                domain.getCountry(),
                domain.getHireDate(),
                salary.getCurrency(),
                salary.getBaseSalary(),
                salary.getHousingAllowance(),
                salary.getTransportAllowance(),
                salary.getPerformanceBonus(),
                salary.getStatutoryDeduction(),
                salary.calculateGrossAnnual(),
                salary.calculateNetAnnual(),
                domain.getAnnualGrossUSD(),
                domain.getAnnualNetUSD()
        );
    }
}

