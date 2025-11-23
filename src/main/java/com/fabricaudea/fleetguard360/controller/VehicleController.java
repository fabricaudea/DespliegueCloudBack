package com.fabricaudea.fleetguard360.controller;


import com.fabricaudea.fleetguard360.dto.CreateVehicleDTO;
import com.fabricaudea.fleetguard360.dto.VehicleDTO;
import com.fabricaudea.fleetguard360.dto.VehicleLocationUpdateDTO;
import com.fabricaudea.fleetguard360.entity.VehicleStatus;
import com.fabricaudea.fleetguard360.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Vehicle Management API")
@Slf4j
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // ===== GET =====

    @GetMapping
    @Operation(summary = "Get all vehicles")
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        log.info("Getting all vehicles");
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        log.info("Getting vehicle: {}", id);
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get vehicles by status")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByStatus(@PathVariable VehicleStatus status) {
        log.info("Getting vehicles with status: {}", status);
        return ResponseEntity.ok(vehicleService.getVehiclesByStatus(status));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available vehicles")
    public ResponseEntity<List<VehicleDTO>> getAvailableVehicles() {
        log.info("Getting available vehicles");
        return ResponseEntity.ok(vehicleService.getAvailableVehicles());
    }

    // ===== POST =====

    @PostMapping
    @Operation(summary = "Create new vehicle")
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody CreateVehicleDTO dto) {
        log.info("Creating vehicle: {}", dto.getLicensePlate());
        VehicleDTO created = vehicleService.createVehicle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/location")
    @Operation(summary = "Update vehicle location (broadcasts to SSE clients)")
    public ResponseEntity<VehicleDTO> updateVehicleLocation(
            @PathVariable Long id,
            @Valid @RequestBody VehicleLocationUpdateDTO dto) {
        log.info("Updating location for vehicle: {}", id);
        VehicleDTO updated = vehicleService.updateVehicleLocation(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ===== PUT =====

    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle details")
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody CreateVehicleDTO dto) {
        log.info("Updating vehicle: {}", id);
        VehicleDTO updated = vehicleService.updateVehicle(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ===== PATCH =====

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update vehicle status")
    public ResponseEntity<VehicleDTO> updateVehicleStatus(
            @PathVariable Long id,
            @RequestParam VehicleStatus status) {
        log.info("Updating status for vehicle: {} to {}", id, status);
        VehicleDTO updated = vehicleService.updateVehicleStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // ===== DELETE =====

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        log.info("Deleting vehicle: {}", id);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
