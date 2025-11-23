package com.fabricaudea.fleetguard360.service;

import com.fabricaudea.fleetguard360.dto.VehicleDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Registra un nuevo cliente SSE
     */
    public SseEmitter registerClient() {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minutos de timeout
        emitters.add(emitter);

        // Manejar desconexión
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("SSE client disconnected. Active clients: {}", emitters.size());
        });

        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            log.info("SSE client timeout. Active clients: {}", emitters.size());
        });

        emitter.onError(throwable -> {
            emitters.remove(emitter);
            log.error("SSE client error", throwable);
        });

        log.info("SSE client registered. Total clients: {}", emitters.size());
        return emitter;
    }

    /**
     * Envía una actualización de ubicación a todos los clientes conectados
     */
    public void broadcastVehicleUpdate(VehicleDTO vehicle) {
        List<SseEmitter> emittersToRemove = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .id(String.valueOf(vehicle.getId()))
                        .name("vehicleUpdate")
                        .data(vehicle)
                        .reconnectTime(5000); // Reconectar en 5 segundos si falla

                emitter.send(event);
            } catch (IOException e) {
                emittersToRemove.add(emitter);
                log.debug("Error sending SSE event to client", e);
            }
        }

        emittersToRemove.forEach(emitters::remove);
    }

    public int getActiveClientsCount() {
        return emitters.size();
    }
}
