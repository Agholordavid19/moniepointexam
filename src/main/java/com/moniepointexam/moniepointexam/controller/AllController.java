package com.moniepointexam.moniepointexam.controller;

import com.moniepointexam.moniepointexam.service.AnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@AllArgsConstructor //to inject the final dependency
public class AllController {
    private final AnalyticsService service;

    @GetMapping("/top-merchant")
    public ResponseEntity<Map<String, Object>> topMerchant() {
        return ResponseEntity.ok(service.getTopMerchant());
    }

    @GetMapping("/monthly-active-merchants")
    public ResponseEntity<Map<String, Long>> monthlyActiveMerchants() {
        return ResponseEntity.ok(service.getMonthlyActiveMerchants());
    }

    @GetMapping("/product-adoption")
    public ResponseEntity<Map<String, Long>> productAdoption() {
        return ResponseEntity.ok(service.getProductAdoption());
    }

    @GetMapping("/kyc-funnel")
    public ResponseEntity<Map<String, Long>> kycFunnel() {
        return ResponseEntity.ok(service.getKycFunnel());
    }

    @GetMapping("/failure-rates")
    public ResponseEntity<List<Map<String, Object>>> failureRates() {
        return ResponseEntity.ok(service.getFailureRates());
    }
}