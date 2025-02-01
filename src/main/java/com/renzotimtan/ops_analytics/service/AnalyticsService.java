package com.renzotimtan.ops_analytics.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import com.renzotimtan.ops_analytics.model.OrderEvent;
import com.renzotimtan.ops_analytics.repository.AnalyticsSnapshotRepository;

@Service
public class AnalyticsService {

    private final AtomicLong totalOrders = new AtomicLong(0);
    private final AtomicReference<Double> totalRevenue = new AtomicReference<>(0.0);
    private final ConcurrentHashMap<String, Long> productSales = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> customerSpending = new ConcurrentHashMap<>();

    private final AnalyticsSnapshotRepository snapshotRepository;

    public AnalyticsService(AnalyticsSnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;

        snapshotRepository.findTopByOrderByTimestampDesc().ifPresent(snapshot -> {
            totalOrders.set(snapshot.getTotalOrders());
            totalRevenue.set(snapshot.getTotalRevenue());
            productSales.putAll(snapshot.getProductSales());
            customerSpending.putAll(snapshot.getCustomerSpending());
        });
    }

    public void processOrder(OrderEvent orderEvent) {
        // Increment total order count
        totalOrders.incrementAndGet();

        // Update total revenue
        double currentOrderAmount = orderEvent.getTotalAmount();
        totalRevenue.updateAndGet(val -> val + currentOrderAmount);

        // Track per-user spending
        customerSpending.merge(orderEvent.getUser().getName(), currentOrderAmount, Double::sum);

        // Update product sales
        Map<String, Integer> productQuantities = orderEvent.getProductQuantities();
        if (productQuantities != null) {
            productQuantities.forEach((productId, quantity) -> 
                productSales.merge(productId, (long)quantity, Long::sum)
            );
        }
    }

    // **METRICS BELOW**

    // Real-Time Order Volume
    public long getTotalOrders() {
        return totalOrders.get();
    }

    // Real-Time Revenue
    public double getTotalRevenue() {
        return totalRevenue.get();
    }

    // Average Order Value (AOV)
    public double getAverageOrderValue() {
        long count = getTotalOrders();
        if (count == 0) {
            return 0.0;
        }
        return getTotalRevenue() / count;
    }

    // Top K Customers
    public Map<String, Double> getTopCustomers(int limit) {
        List<Map.Entry<String, Double>> sortedEntries = customerSpending.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(limit)
            .toList();

        // Use linkedHashmap to maintain order
        Map<String, Double> topCustomers = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : sortedEntries) {
            topCustomers.put(entry.getKey(), entry.getValue());
        }

        return topCustomers;
    }

    // Top K Selling Products
    public Map<String, Long> getTopProducts(int limit) {
        List<Map.Entry<String, Long>> sortedEntries = productSales.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(limit)
            .toList();

        // Use linkedHashmap to maintain order
        Map<String, Long> topProducts = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : sortedEntries) {
            topProducts.put(entry.getKey(), entry.getValue());
        }

        return topProducts;
    }

    // Average # of Order per Day
    public double getAverageOrdersPerDay() {

        long totalOrderCount = getTotalOrders();
        if (totalOrderCount == 0) {
            return 0.0;
        }
    
        // Find the first order date from snapshots or orders
        LocalDate firstOrderDate = snapshotRepository.findFirstByOrderByTimestampAsc()
            .map(snapshot -> snapshot.getTimestamp().toLocalDate())
            .orElse(LocalDate.now()); // Default to today if no snapshot exists
    
        // Calculate days since first order
        long daysSinceFirstOrder = ChronoUnit.DAYS.between(firstOrderDate, LocalDate.now());
        if (daysSinceFirstOrder == 0) {
            return totalOrderCount;
        }
    
        return (double) totalOrderCount / daysSinceFirstOrder;
    }
}
