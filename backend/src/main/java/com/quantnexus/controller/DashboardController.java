package com.quantnexus.controller;

import com.quantnexus.dto.financial.DashboardSummaryDTO;
import com.quantnexus.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Gateway for Financial Intelligence.
 * Aggregates high-level metrics, trends, and health scores for the primary UI dashboard.
 * @author Manish Singh
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Compiles a comprehensive financial pulse for the user.
     * Consolidates totals, category splits, and trend analysis into a single optimized payload.
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary(@RequestParam Long userId) {
        log.info("API: Compiling real-time financial intelligence summary for User: {}", userId);
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
