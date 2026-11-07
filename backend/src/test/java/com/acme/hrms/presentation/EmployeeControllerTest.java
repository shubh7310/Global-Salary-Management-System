package com.acme.hrms.presentation;

import com.acme.hrms.application.dto.EmployeeResponse;
import com.acme.hrms.application.dto.EmployeeSummaryResponse;
import com.acme.hrms.application.dto.PagedResponse;
import com.acme.hrms.application.dto.SalaryStructureDto;
import com.acme.hrms.application.service.EmployeeService;
import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.Department;
import com.acme.hrms.presentation.controller.EmployeeController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    @DisplayName("Should return paginated employee list")
    void shouldReturnPaginatedEmployees() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeSummaryResponse summary = new EmployeeSummaryResponse(
                id,
                "EMP-00001",
                "Alexander Anderson",
                Department.ENGINEERING,
                "Senior Software Craftsperson",
                Country.UNITED_STATES,
                Currency.USD,
                new BigDecimal("140000.00"),
                new BigDecimal("112000.00"),
                new BigDecimal("140000.00"),
                new BigDecimal("112000.00")
        );

        PagedResponse<EmployeeSummaryResponse> pagedResponse = new PagedResponse<>(
                List.of(summary), 0, 20, 1L, 1, true, true
        );

        when(employeeService.searchEmployees(any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].employeeCode").value("EMP-00001"))
                .andExpect(jsonPath("$.content[0].fullName").value("Alexander Anderson"));
    }

    @Test
    @DisplayName("Should return single employee detail with salary structure")
    void shouldReturnSingleEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        SalaryStructureDto salaryDto = new SalaryStructureDto(
                Currency.USD,
                new BigDecimal("100000.00"),
                new BigDecimal("15000.00"),
                new BigDecimal("5000.00"),
                new BigDecimal("20000.00"),
                new BigDecimal("28000.00"),
                new BigDecimal("140000.00"),
                new BigDecimal("112000.00"),
                new BigDecimal("11666.67"),
                new BigDecimal("9333.33"),
                new BigDecimal("140000.00"),
                new BigDecimal("112000.00")
        );

        EmployeeResponse response = new EmployeeResponse(
                id,
                "EMP-00001",
                "Alexander",
                "Anderson",
                "Alexander Anderson",
                "alexander.anderson@acme.com",
                Department.ENGINEERING,
                "Engineering",
                "Senior Software Craftsperson",
                Country.UNITED_STATES,
                "United States",
                LocalDate.of(2021, 5, 10),
                salaryDto
        );

        when(employeeService.getEmployeeById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/employees/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode").value("EMP-00001"))
                .andExpect(jsonPath("$.salaryStructure.currency").value("USD"))
                .andExpect(jsonPath("$.salaryStructure.grossAnnualLocal").value(140000.00));
    }
}

