package com.fabricaudea.fleetguard360.service;


import com.fabricaudea.fleetguard360.dto.CreateVehicleDTO;
import com.fabricaudea.fleetguard360.dto.VehicleDTO;
import com.fabricaudea.fleetguard360.dto.VehicleLocationUpdateDTO;
import com.fabricaudea.fleetguard360.entity.Vehicle;
import com.fabricaudea.fleetguard360.entity.VehicleStatus;
import com.fabricaudea.fleetguard360.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SseService sseService;

    // ===== CRUD Operations =====

    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));
        return toDTO(vehicle);
    }

    public VehicleDTO createVehicle(CreateVehicleDTO dto) {
        // Verificar que no exista otra con la misma placa
        vehicleRepository.findByLicensePlate(dto.getLicensePlate())
                .ifPresent(v -> {
                    throw new RuntimeException("License plate already exists: " + dto.getLicensePlate());
                });

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setModel(dto.getModel());
        vehicle.setCapacity(dto.getCapacity());
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setLatitude(0.0);
        vehicle.setLongitude(0.0);
        vehicle.setSpeed(0.0);
        vehicle.setHeading(0.0);
        vehicle.setLastUpdate(LocalDateTime.now());

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle created: {}", saved.getId());

        return toDTO(saved);
    }

    public VehicleDTO updateVehicle(Long id, CreateVehicleDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));

        vehicle.setModel(dto.getModel());
        vehicle.setCapacity(dto.getCapacity());
        vehicle.setLastUpdate(LocalDateTime.now());

        Vehicle updated = vehicleRepository.save(vehicle);
        log.info("Vehicle updated: {}", id);

        return toDTO(updated);
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Vehicle not found: " + id);
        }
        vehicleRepository.deleteById(id);
        log.info("Vehicle deleted: {}", id);
    }

    // ===== Location Update (with SSE broadcast) =====

    public VehicleDTO updateVehicleLocation(Long id, VehicleLocationUpdateDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));

        vehicle.setLatitude(dto.getLatitude());
        vehicle.setLongitude(dto.getLongitude());
        vehicle.setSpeed(dto.getSpeed());
        vehicle.setHeading(dto.getHeading());
        vehicle.setLastUpdate(LocalDateTime.now());

        Vehicle updated = vehicleRepository.save(vehicle);

        // Broadcast update to all connected SSE clients
        VehicleDTO updatedDTO = toDTO(updated);
        sseService.broadcastVehicleUpdate(updatedDTO);

        log.debug("Vehicle location updated: {} - Lat: {}, Lon: {}",
                id, dto.getLatitude(), dto.getLongitude());

        return updatedDTO;
    }

    public List<VehicleDTO> getVehiclesByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleDTO> getAvailableVehicles() {
        return getVehiclesByStatus(VehicleStatus.AVAILABLE);
    }

    public VehicleDTO updateVehicleStatus(Long id, VehicleStatus status) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));

        vehicle.setStatus(status);
        vehicle.setLastUpdate(LocalDateTime.now());

        Vehicle updated = vehicleRepository.save(vehicle);

        // Broadcast status change
        sseService.broadcastVehicleUpdate(toDTO(updated));

        log.info("Vehicle status updated: {} - Status: {}", id, status);

        return toDTO(updated);
    }

    // ===== Helper Methods =====

    private VehicleDTO toDTO(Vehicle vehicle) {
        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                vehicle.getModel(),
                vehicle.getCapacity(),
                vehicle.getStatus(),
                vehicle.getLatitude(),
                vehicle.getLongitude(),
                vehicle.getSpeed(),
                vehicle.getHeading(),
                vehicle.getLastUpdate()
        );
    }
}
