package com.fabricaudea.fleetguard360.controller;

import com.fabricaudea.fleetguard360.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Service health endpoint")
@Slf4j
@CrossOrigin(origins = "*")
public class HealthController {

    @Autowired
    private SseService sseService;

    @GetMapping
    @Operation(summary = "Check service health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "FleetGuard360 Monitoring Service");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("activeSSEClients", sseService.getActiveClientsCount());

        return ResponseEntity.ok(response);
    }
}
