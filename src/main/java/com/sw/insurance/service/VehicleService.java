package com.sw.insurance.service;

import com.sw.insurance.dto.VehicleResponse;
import com.sw.insurance.entity.Vehicle;
import com.sw.insurance.exception.ResourceNotFoundException;
import com.sw.insurance.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleResponse getVehicleByRegistrationNumber(String registrationNumber) {
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with registration number: " + registrationNumber));
        
        return mapToVehicleResponse(vehicle);
    }

    private VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .registrationNumber(vehicle.getRegistrationNumber())
                .vin(vehicle.getVin())
                .ownerPersonalId(vehicle.getOwnerPersonalId())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .color(vehicle.getColor())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
