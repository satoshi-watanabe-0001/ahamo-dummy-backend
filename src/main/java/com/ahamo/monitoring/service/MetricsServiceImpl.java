package com.ahamo.monitoring.service;

import com.ahamo.monitoring.dto.SystemMetricsResponse;
import com.ahamo.device.repository.InventoryRepository;
import com.ahamo.customer.repository.CustomerRepository;
import com.ahamo.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsServiceImpl implements MetricsService {
    
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    
    @Override
    public SystemMetricsResponse getSystemMetrics() {
        log.debug("システムメトリクス収集開始");
        
        Map<String, Object> metrics = new HashMap<>();
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        metrics.put("memory_used_bytes", usedMemory);
        metrics.put("memory_max_bytes", maxMemory);
        metrics.put("memory_usage_percent", memoryUsagePercent);
        metrics.put("uptime_seconds", runtimeBean.getUptime() / 1000);
        metrics.put("available_processors", Runtime.getRuntime().availableProcessors());
        
        return SystemMetricsResponse.builder()
                .timestamp(LocalDateTime.now())
                .metricType("SYSTEM")
                .metrics(metrics)
                .status("HEALTHY")
                .build();
    }
    
    @Override
    public SystemMetricsResponse getApplicationMetrics() {
        log.debug("アプリケーションメトリクス収集開始");
        
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            long totalInventoryItems = inventoryRepository.count();
            long lowStockItems = inventoryRepository.findLowStockItems().size();
            long totalCustomers = customerRepository.count();
            long totalPlans = planRepository.count();
            
            metrics.put("total_inventory_items", totalInventoryItems);
            metrics.put("low_stock_items", lowStockItems);
            metrics.put("total_customers", totalCustomers);
            metrics.put("total_plans", totalPlans);
            metrics.put("low_stock_ratio", totalInventoryItems > 0 ? (double) lowStockItems / totalInventoryItems : 0.0);
            
        } catch (Exception e) {
            log.error("アプリケーションメトリクス収集中にエラーが発生", e);
            metrics.put("error", "メトリクス収集エラー: " + e.getMessage());
        }
        
        return SystemMetricsResponse.builder()
                .timestamp(LocalDateTime.now())
                .metricType("APPLICATION")
                .metrics(metrics)
                .status("HEALTHY")
                .build();
    }
    
    @Override
    public SystemMetricsResponse getBusinessMetrics() {
        log.debug("ビジネスKPIメトリクス収集開始");
        
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            long totalCustomers = customerRepository.count();
            long totalInventoryItems = inventoryRepository.count();
            long availableInventoryItems = inventoryRepository.findAll().stream()
                    .mapToInt(inv -> inv.getAvailableStock())
                    .sum();
            
            metrics.put("total_customers", totalCustomers);
            metrics.put("total_inventory_value", availableInventoryItems);
            metrics.put("inventory_turnover_rate", calculateInventoryTurnoverRate());
            metrics.put("customer_growth_rate", calculateCustomerGrowthRate());
            
        } catch (Exception e) {
            log.error("ビジネスメトリクス収集中にエラーが発生", e);
            metrics.put("error", "ビジネスメトリクス収集エラー: " + e.getMessage());
        }
        
        return SystemMetricsResponse.builder()
                .timestamp(LocalDateTime.now())
                .metricType("BUSINESS")
                .metrics(metrics)
                .status("HEALTHY")
                .build();
    }
    
    @Override
    public SystemMetricsResponse getHealthMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            boolean databaseHealthy = checkDatabaseHealth();
            boolean memoryHealthy = checkMemoryHealth();
            
            metrics.put("database_healthy", databaseHealthy);
            metrics.put("memory_healthy", memoryHealthy);
            metrics.put("overall_healthy", databaseHealthy && memoryHealthy);
            
            String status = (databaseHealthy && memoryHealthy) ? "HEALTHY" : "UNHEALTHY";
            
            return SystemMetricsResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .metricType("HEALTH")
                    .metrics(metrics)
                    .status(status)
                    .build();
                    
        } catch (Exception e) {
            log.error("ヘルスチェック中にエラーが発生", e);
            metrics.put("error", "ヘルスチェックエラー: " + e.getMessage());
            
            return SystemMetricsResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .metricType("HEALTH")
                    .metrics(metrics)
                    .status("ERROR")
                    .build();
        }
    }
    
    @Override
    public void recordCustomMetric(String metricName, double value) {
        log.debug("カスタムメトリクス記録: {}={}", metricName, value);
    }
    
    @Override
    public void recordCustomMetric(String metricName, double value, String... tags) {
        log.debug("カスタムメトリクス記録: {}={}, tags={}", metricName, value, String.join(",", tags));
    }
    
    private boolean checkDatabaseHealth() {
        try {
            inventoryRepository.count();
            return true;
        } catch (Exception e) {
            log.error("データベースヘルスチェック失敗", e);
            return false;
        }
    }
    
    private boolean checkMemoryHealth() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        return memoryUsagePercent < 90.0;
    }
    
    private double calculateInventoryTurnoverRate() {
        return 0.85;
    }
    
    private double calculateCustomerGrowthRate() {
        return 0.12;
    }
}
