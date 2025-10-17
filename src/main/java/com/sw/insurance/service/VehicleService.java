package com.sw.insurance.service;

import com.sw.insurance.dto.VehicleResponse;
import com.sw.insurance.entity.Vehicle;
import com.sw.insurance.exception.FeatureNotAvailableException;
import com.sw.insurance.exception.ResourceNotFoundException;
import com.sw.insurance.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final FeatureFlagService featureFlagService;
    private static final String SW_INSURANCE_CAR_AVAILABLE = "sw-insurance-car-available";

    public VehicleResponse getVehicleByRegistrationNumber(String registrationNumber) {
        boolean isSWInsuranceCarAvailable = featureFlagService.isFeatureEnabled(
                SW_INSURANCE_CAR_AVAILABLE, false);

        log.info("SW Insurance Car product is {}",
                isSWInsuranceCarAvailable ? "enabled" : "disabled");

        if (!isSWInsuranceCarAvailable) {
            throw new FeatureNotAvailableException("Car insurance feature is not supported!");
        }

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
