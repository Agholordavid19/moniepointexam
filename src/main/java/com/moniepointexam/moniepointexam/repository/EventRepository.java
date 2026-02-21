package com.moniepointexam.moniepointexam.repository;

import com.moniepointexam.moniepointexam.model.entity.Event;
import com.moniepointexam.moniepointexam.model.enums.Product;
import com.moniepointexam.moniepointexam.model.enums.Status;
import com.moniepointexam.moniepointexam.service.MerchantVolume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, java.util.UUID> {

    // KYC funnel (derived query)
    @Query("SELECT COUNT(DISTINCT e.merchantId) FROM Event e " +
            "WHERE e.product = :product AND e.eventType = :eventType AND e.status = :status")
    Long countDistinctMerchantsByProductAndEventTypeAndStatus(
            @Param("product") Product product,
            @Param("eventType") String eventType,
            @Param("status") Status status);

    // Product adoption (projection)
    interface ProductAdoption {
        String getProduct();
        long getMerchantCount();
    }

    @Query("SELECT e.product AS product, COUNT(DISTINCT e.merchantId) AS merchantCount " +
            "FROM Event e GROUP BY e.product ORDER BY merchantCount DESC")
    List<ProductAdoption> findProductAdoption();

    // Top merchant (projection)
    @Query("SELECT e.merchantId AS merchantId, SUM(e.amount) AS totalVolume " +
            "FROM Event e WHERE e.status = 'SUCCESS' " +
            "GROUP BY e.merchantId ORDER BY totalVolume DESC")
    List<MerchantVolume> findTopMerchants();

    // Failure rates per product (projection)
    interface ProductFailureRate {
        String getProduct();
        Double getFailureRate();
    }

    @Query("SELECT e.product AS product, " +
            "(SUM(CASE WHEN e.status='FAILED' THEN 1 ELSE 0 END) * 100.0 / COUNT(e)) AS failureRate " +
            "FROM Event e WHERE e.status <> 'PENDING' " +
            "GROUP BY e.product ORDER BY failureRate DESC")
    List<ProductFailureRate> findFailureRates();

    @Query("SELECT EXTRACT(YEAR FROM e.eventTimestamp), EXTRACT(MONTH FROM e.eventTimestamp), COUNT(DISTINCT e.merchantId) " +
            "FROM Event e WHERE e.status = 'SUCCESS' " +
            "GROUP BY EXTRACT(YEAR FROM e.eventTimestamp), EXTRACT(MONTH FROM e.eventTimestamp) " +
            "ORDER BY EXTRACT(YEAR FROM e.eventTimestamp), EXTRACT(MONTH FROM e.eventTimestamp)")
    List<Object[]> findMonthlyActiveMerchants();
}