package com.renzotimtan.ops_analytics.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.renzotimtan.ops_analytics.model.AnalyticsSnapshot;

@Repository
public interface AnalyticsSnapshotRepository extends MongoRepository<AnalyticsSnapshot, String> {
    
    // Find the most recent snapshot by timestamp (MongoDB sorts by descending order)
    Optional<AnalyticsSnapshot> findTopByOrderByTimestampDesc();
    Optional<AnalyticsSnapshot> findFirstByOrderByTimestampAsc();
}
