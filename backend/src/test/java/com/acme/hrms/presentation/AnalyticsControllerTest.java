package com.acme.hrms.presentation;

import com.acme.hrms.application.dto.AdjustmentSimulationRequest;
import com.acme.hrms.application.dto.AdjustmentSimulationResponse;
import com.acme.hrms.application.dto.AnalyticsOverviewResponse;
import com.acme.hrms.application.service.CompensationAnalyticsService;
import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Department;
import com.acme.hrms.presentation.controller.AnalyticsController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompensationAnalyticsService analyticsService;

    @Test
    @DisplayName("Should return analytics overview KPIs")
    void shouldReturnAnalyticsOverview() throws Exception {
        AnalyticsOverviewResponse overview = new AnalyticsOverviewResponse(
                10000L,
                new BigDecimal("1250000000.00"),
                new BigDecimal("125000.00"),
                new BigDecimal("118000.00"),
                new BigDecimal("45000.00"),
                new BigDecimal("340000.00")
        );

        when(analyticsService.getOverview(any(), any())).thenReturn(overview);

        mockMvc.perform(get("/api/v1/analytics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHeadcount").value(10000))
                .andExpect(jsonPath("$.totalPayrollUSD").value(1250000000.00))
                .andExpect(jsonPath("$.averageSalaryUSD").value(125000.00))
                .andExpect(jsonPath("$.medianSalaryUSD").value(118000.00));
    }

    @Test
    @DisplayName("Should simulate salary adjustment")
    void shouldSimulateAdjustment() throws Exception {
        AdjustmentSimulationRequest request = new AdjustmentSimulationRequest(
                Country.GERMANY,
                Department.ENGINEERING,
                new BigDecimal("5.00")
        );

        AdjustmentSimulationResponse response = new AdjustmentSimulationResponse(
                Country.GERMANY,
                Department.ENGINEERING,
                new BigDecimal("5.00"),
                450L,
                com.acme.hrms.domain.model.Currency.EUR,
                new BigDecimal("50000000.00"),
                new BigDecimal("52500000.00"),
                new BigDecimal("2500000.00"),
                new BigDecimal("54000000.00"),
                new BigDecimal("56700000.00"),
                new BigDecimal("2700000.00"),
                new BigDecimal("120000.00"),
                new BigDecimal("126000.00")
        );

        when(analyticsService.simulateAdjustment(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/analytics/simulate-adjustment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.affectedEmployees").value(450))
                .andExpect(jsonPath("$.payrollDeltaUSD").value(2700000.00))
                .andExpect(jsonPath("$.projectedAvgSalaryUSD").value(126000.00));
    }
}

