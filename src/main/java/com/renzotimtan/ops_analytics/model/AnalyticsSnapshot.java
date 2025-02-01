package com.renzotimtan.ops_analytics.model;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "analytics_snapshots")
public class AnalyticsSnapshot {
    @Id
    private String id;
    private long totalOrders;
    private double totalRevenue;
    private Map<String, Long> productSales;
    private Map<String, Double> customerSpending;
    private LocalDateTime timestamp;
}