package com.sw.insurance.unit;

import com.sw.insurance.dto.VehicleResponse;
import com.sw.insurance.entity.Vehicle;
import com.sw.insurance.exception.ResourceNotFoundException;
import com.sw.insurance.repository.VehicleRepository;
import com.sw.insurance.service.FeatureFlagService;
import com.sw.insurance.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private FeatureFlagService featureFlagService;

    @InjectMocks
    private VehicleService vehicleService;

    private static final String FEATURE_FLAG_KEY = "sw-insurance-pet-available";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Default behavior for feature flag
        when(featureFlagService.isFeatureEnabled(eq(FEATURE_FLAG_KEY), anyBoolean()))
            .thenReturn(false);
    }

    @Test
    void getVehicleByRegistrationNumber_ShouldReturnVehicle_WhenExists() {
        // Arrange
        String registrationNumber = "ABC123";
        Vehicle vehicle = createTestVehicle(registrationNumber);
        when(vehicleRepository.findByRegistrationNumber(registrationNumber))
                .thenReturn(Optional.of(vehicle));

        // Act
        VehicleResponse response = vehicleService.getVehicleByRegistrationNumber(registrationNumber);

        // Assert
        assertNotNull(response);
        assertEquals(registrationNumber, response.getRegistrationNumber());
        assertEquals("Toyota", response.getMake());
        verify(vehicleRepository, times(1)).findByRegistrationNumber(registrationNumber);
    }

    @Test
    void getVehicleByRegistrationNumber_ShouldThrowException_WhenNotFound() {
        // Arrange
        String registrationNumber = "NONEXISTENT";
        when(vehicleRepository.findByRegistrationNumber(registrationNumber))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.getVehicleByRegistrationNumber(registrationNumber);
        });
        verify(vehicleRepository, times(1)).findByRegistrationNumber(registrationNumber);
    }

    private Vehicle createTestVehicle(String registrationNumber) {
        return Vehicle.builder()
                .registrationNumber(registrationNumber)
                .vin("1HGCM82633A123456")
                .ownerPersonalId("ID12345678")
                .make("Toyota")
                .model("Camry")
                .year(2020)
                .color("Silver")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
