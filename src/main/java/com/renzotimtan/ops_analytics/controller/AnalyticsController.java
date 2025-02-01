package com.renzotimtan.ops_analytics.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renzotimtan.ops_analytics.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalOrders", analyticsService.getTotalOrders());
        response.put("totalRevenue", analyticsService.getTotalRevenue());
        response.put("averageOrderValue", analyticsService.getAverageOrderValue());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-products")
    public ResponseEntity<Map<String, Long>> getTopProducts(@RequestParam(defaultValue = "5") int limit) {
        Map<String, Long> topProducts = analyticsService.getTopProducts(limit);

        if (topProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(topProducts);
        }

        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/top-customers")
    public ResponseEntity<Map<String, Double>> getTopCustomers(@RequestParam(defaultValue = "5") int limit) {
        Map<String, Double> topCustomers = analyticsService.getTopCustomers(limit);

        if (topCustomers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(topCustomers);
        }

        return ResponseEntity.ok(topCustomers);
    }
}
