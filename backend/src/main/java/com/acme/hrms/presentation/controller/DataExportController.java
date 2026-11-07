package com.acme.hrms.presentation.controller;

import com.acme.hrms.application.service.EmployeeService;
import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Department;
import com.acme.hrms.infrastructure.persistence.entity.EmployeeJpaEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees/export")
@CrossOrigin(origins = "*")
public class DataExportController {

    private final EmployeeService employeeService;

    public DataExportController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Country country,
            @RequestParam(required = false) Department department,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary) {

        List<EmployeeJpaEntity> employees = employeeService.getEmployeesForExport(search, country, department, minSalary, maxSalary);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("EmployeeCode,FirstName,LastName,Email,Department,JobTitle,Country,HireDate,Currency,BaseSalary,HousingAllowance,TransportAllowance,PerformanceBonus,Deduction,GrossLocal,NetLocal,GrossUSD,NetUSD");

        for (EmployeeJpaEntity e : employees) {
            pw.printf("%s,%s,%s,%s,%s,\"%s\",%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    escape(e.getEmployeeCode()),
                    escape(e.getFirstName()),
                    escape(e.getLastName()),
                    escape(e.getEmail()),
                    e.getDepartment().name(),
                    escape(e.getJobTitle()),
                    e.getCountry().name(),
                    e.getHireDate(),
                    e.getCurrency().name(),
                    e.getBaseSalary(),
                    e.getHousingAllowance(),
                    e.getTransportAllowance(),
                    e.getPerformanceBonus(),
                    e.getStatutoryDeduction(),
                    e.getAnnualGrossLocal(),
                    e.getAnnualNetLocal(),
                    e.getAnnualGrossUSD(),
                    e.getAnnualNetUSD());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "acme_employees_salary_export.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(sw.toString());
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}

