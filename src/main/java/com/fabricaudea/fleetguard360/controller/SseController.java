package com.fabricaudea.fleetguard360.controller;

import com.fabricaudea.fleetguard360.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/stream")
@Tag(name = "Real-time Streaming", description = "Server-Sent Events (SSE) for real-time vehicle updates")
@Slf4j
@CrossOrigin(origins = "*")
public class SseController {

    @Autowired
    private SseService sseService;

    @GetMapping(path = "/vehicles", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to vehicle updates via SSE")
    public SseEmitter subscribeToVehicleUpdates() {
        log.info("New SSE client connected");
        return sseService.registerClient();
    }
}
