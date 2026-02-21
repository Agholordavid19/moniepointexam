package com.moniepointexam.moniepointexam.service;

import com.moniepointexam.moniepointexam.model.enums.Product;
import com.moniepointexam.moniepointexam.model.enums.Status;
import com.moniepointexam.moniepointexam.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class AnalyticsService {
    //the dependency that connects to the database
    private final EventRepository repository;

    // Top merchant
    public Map<String, Object> getTopMerchant() {
        List<MerchantVolume> topList = repository.findTopMerchants();
        if (topList.isEmpty()) return Collections.emptyMap();

        MerchantVolume top = topList.get(0);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("merchant_id", top.getMerchantId());
        map.put("total_volume", top.getTotalVolume());
        return map;
    }

    // the Monthly active merchants
    public Map<String, Long> getMonthlyActiveMerchants() {
        Map<String, Long> result = new LinkedHashMap<>();
        List<Object[]> list = repository.findMonthlyActiveMerchants();
        for (Object[] row : list) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            String key = String.format("%d-%02d", year, month);
            result.put(key, ((Number) row[2]).longValue());
        }
        return result;
    }

    // the Product adoption
    public Map<String, Long> getProductAdoption() {
        Map<String, Long> result = new LinkedHashMap<>();
        repository.findProductAdoption().forEach(adoption -> result.put(adoption.getProduct(), adoption.getMerchantCount()));
        return result;
    }

    // KYC  channel through
    public Map<String, Long> getKycFunnel() {
        Map<String, Long> result = new HashMap<>();
        result.put("documents_submitted",
                repository.countDistinctMerchantsByProductAndEventTypeAndStatus(Product.KYC, "DOCUMENT_SUBMITTED", Status.SUCCESS));
        result.put("verifications_completed",
                repository.countDistinctMerchantsByProductAndEventTypeAndStatus(Product.KYC, "VERIFICATION_COMPLETED", Status.SUCCESS));
        result.put("tier_upgrades",
                repository.countDistinctMerchantsByProductAndEventTypeAndStatus(Product.KYC, "TIER_UPGRADE", Status.SUCCESS));
        return result;
    }

    // the Failure rates
    public List<Map<String, Object>> getFailureRates() {
        List<Map<String, Object>> result = new ArrayList<>();
        repository.findFailureRates().forEach(pr -> {
            Map<String, Object> map = new HashMap<>();
            map.put("product", pr.getProduct());
            map.put("failure_rate", Math.round(pr.getFailureRate() * 10.0) / 10.0); // 1 decimal
            result.add(map);
        });
        return result;
    }
}