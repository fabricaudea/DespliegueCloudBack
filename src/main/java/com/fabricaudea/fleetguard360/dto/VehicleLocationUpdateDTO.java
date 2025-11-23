package com.fabricaudea.fleetguard360.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocationUpdateDTO {
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
}