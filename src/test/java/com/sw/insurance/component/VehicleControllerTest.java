package com.sw.insurance.component;

import com.sw.insurance.controller.VehicleController;
import com.sw.insurance.dto.VehicleResponse;
import com.sw.insurance.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getVehicleByRegistrationNumber_ShouldReturnOk_WhenVehicleExists() {
        // Arrange
        String registrationNumber = "ABC123";
        VehicleResponse mockResponse = VehicleResponse.builder()
                .registrationNumber(registrationNumber)
                .make("Toyota")
                .model("Camry")
                .build();

        when(vehicleService.getVehicleByRegistrationNumber(registrationNumber))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<VehicleResponse> response = 
                vehicleController.getVehicleByRegistrationNumber(registrationNumber);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(registrationNumber, response.getBody().getRegistrationNumber());
        verify(vehicleService, times(1)).getVehicleByRegistrationNumber(registrationNumber);
    }

    @Test
    void getVehicleByRegistrationNumber_ShouldReturn404_WhenVehicleNotFound() {
        // Arrange
        String registrationNumber = "NONEXISTENT";
        when(vehicleService.getVehicleByRegistrationNumber(registrationNumber))
                .thenThrow(new RuntimeException("Vehicle not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            vehicleController.getVehicleByRegistrationNumber(registrationNumber);
        });
        verify(vehicleService, times(1)).getVehicleByRegistrationNumber(registrationNumber);
    }
}
