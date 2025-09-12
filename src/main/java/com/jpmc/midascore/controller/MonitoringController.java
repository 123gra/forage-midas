package com.jpmc.midascore.controller;

import com.jpmc.midascore.service.TransactionMonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for monitoring transaction processing
 */
@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {
    
    private final TransactionMonitoringService monitoringService;
    
    public MonitoringController(TransactionMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }
    
    /**
     * Get transaction processing statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<TransactionMonitoringService.TransactionStats> getTransactionStats() {
        TransactionMonitoringService.TransactionStats stats = monitoringService.getTransactionStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Trigger manual check of failed transaction threshold
     */
    @GetMapping("/check-failures")
    public ResponseEntity<String> checkFailedTransactions() {
        monitoringService.checkFailedTransactionThreshold();
        return ResponseEntity.ok("Failed transaction threshold check completed. Check logs for alerts.");
    }
}
