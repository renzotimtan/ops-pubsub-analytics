package com.renzotimtan.ops_analytics.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.renzotimtan.ops_analytics.model.AnalyticsSnapshot;

import com.renzotimtan.ops_analytics.repository.AnalyticsSnapshotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsSnapshotService {

    private final AnalyticsService aggregator;
    private final AnalyticsSnapshotRepository repository;

    @Scheduled(cron = "0 */1 * * * *")
    public void saveSnapshot() {
        AnalyticsSnapshot snapshot = new AnalyticsSnapshot();
        snapshot.setId(LocalDateTime.now().toString());
        snapshot.setTotalOrders(aggregator.getTotalOrders());
        snapshot.setTotalRevenue(aggregator.getTotalRevenue());
        snapshot.setProductSales(new LinkedHashMap<>(aggregator.getTopProducts(Integer.MAX_VALUE)));
        snapshot.setCustomerSpending(new LinkedHashMap<>(aggregator.getTopCustomers(Integer.MAX_VALUE)));
        snapshot.setTimestamp(LocalDateTime.now());
        repository.save(snapshot);
    }
}
