package com.fabricaudea.fleetguard360.config;


import com.fabricaudea.fleetguard360.entity.Vehicle;
import com.fabricaudea.fleetguard360.entity.VehicleStatus;
import com.fabricaudea.fleetguard360.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Initializing test data...");

        // Vehicle 1
        Vehicle v1 = new Vehicle();
        v1.setLicensePlate("ABC-123");
        v1.setModel("Mercedes Sprinter 2023");
        v1.setCapacity(12);
        v1.setStatus(VehicleStatus.AVAILABLE);
        v1.setLatitude(40.7128);
        v1.setLongitude(-74.0060);
        v1.setSpeed(0.0);
        v1.setHeading(0.0);
        v1.setLastUpdate(LocalDateTime.now());
        vehicleRepository.save(v1);

        // Vehicle 2
        Vehicle v2 = new Vehicle();
        v2.setLicensePlate("DEF-456");
        v2.setModel("Ford Transit 2022");
        v2.setCapacity(10);
        v2.setStatus(VehicleStatus.MAINTENANCE);
        v2.setLatitude(40.7580);
        v2.setLongitude(-73.9855);
        v2.setSpeed(0.0);
        v2.setHeading(0.0);
        v2.setLastUpdate(LocalDateTime.now());
        vehicleRepository.save(v2);

        // Vehicle 3
        Vehicle v3 = new Vehicle();
        v3.setLicensePlate("GHI-789");
        v3.setModel("Iveco Daily 2023");
        v3.setCapacity(14);
        v3.setStatus(VehicleStatus.AVAILABLE);
        v3.setLatitude(40.7489);
        v3.setLongitude(-73.9680);
        v3.setSpeed(0.0);
        v3.setHeading(0.0);
        v3.setLastUpdate(LocalDateTime.now());
        vehicleRepository.save(v3);

        // Vehicle 4
        Vehicle v4 = new Vehicle();
        v4.setLicensePlate("JKL-012");
        v4.setModel("Volkswagen Crafter 2023");
        v4.setCapacity(15);
        v4.setStatus(VehicleStatus.IN_USE);
        v4.setLatitude(40.7505);
        v4.setLongitude(-73.9680);
        v4.setSpeed(25.5);
        v4.setHeading(180.0);
        v4.setLastUpdate(LocalDateTime.now());
        vehicleRepository.save(v4);

        // Vehicle 5
        Vehicle v5 = new Vehicle();
        v5.setLicensePlate("MNO-345");
        v5.setModel("Renault Master 2022");
        v5.setCapacity(8);
        v5.setStatus(VehicleStatus.AVAILABLE);
        v5.setLatitude(40.7614);
        v5.setLongitude(-73.9776);
        v5.setSpeed(0.0);
        v5.setHeading(0.0);
        v5.setLastUpdate(LocalDateTime.now());
        vehicleRepository.save(v5);

        log.info("✅ 5 test vehicles loaded successfully");
    }
}
