package com.acme.hrms.presentation.controller;

import com.acme.hrms.application.dto.*;
import com.acme.hrms.application.service.CompensationAnalyticsService;
import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Department;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final CompensationAnalyticsService analyticsService;

    public AnalyticsController(CompensationAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public ResponseEntity<AnalyticsOverviewResponse> getOverview(
            @RequestParam(required = false) Country country,
            @RequestParam(required = false) Department department) {
        return ResponseEntity.ok(analyticsService.getOverview(country, department));
    }

    @GetMapping("/by-department")
    public ResponseEntity<List<DepartmentAnalyticsResponse>> getDepartmentAnalytics() {
        return ResponseEntity.ok(analyticsService.getDepartmentAnalytics());
    }

    @GetMapping("/by-country")
    public ResponseEntity<List<CountryAnalyticsResponse>> getCountryAnalytics() {
        return ResponseEntity.ok(analyticsService.getCountryAnalytics());
    }

    @PostMapping("/simulate-adjustment")
    public ResponseEntity<AdjustmentSimulationResponse> simulateAdjustment(
            @Valid @RequestBody AdjustmentSimulationRequest request) {
        return ResponseEntity.ok(analyticsService.simulateAdjustment(request));
    }
}

