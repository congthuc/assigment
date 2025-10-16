package com.sw.insurance.controller;

import com.sw.insurance.dto.VehicleResponse;
import com.sw.insurance.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "APIs for managing vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/{registrationNumber}")
    @Operation(
        summary = "Get vehicle by registration number",
        description = "Returns a single vehicle identified by its registration number",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Vehicle found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = VehicleResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Vehicle not found"
            )
        }
    )
    public ResponseEntity<VehicleResponse> getVehicleByRegistrationNumber(
            @PathVariable String registrationNumber) {
        VehicleResponse response = vehicleService.getVehicleByRegistrationNumber(registrationNumber);
        return ResponseEntity.ok(response);
    }
}
