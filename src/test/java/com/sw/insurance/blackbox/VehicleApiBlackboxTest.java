package com.sw.insurance.blackbox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Blackbox test that tests the running application via HTTP.
 * Make sure the application is running on localhost:8080 before running these tests.
 */
public class VehicleApiBlackboxTest {

    private static final String BASE_URL = "http://localhost:8080/api/v1/vehicles/";
    private static final String VALID_REGISTRATION = "ABC123";
    private static final String INVALID_REGISTRATION = "NONEXISTENT";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    private final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    private HttpHeaders createHeaders() {
        String auth = USERNAME + ":" + PASSWORD;
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());

        System.out.println("Using credentials - Username: " + USERNAME + ", Password: " + PASSWORD);
        System.out.println("Authorization header: " + encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", encodedAuth);
        return headers;
    }

    @Test
    public void getVehicle_WithValidRegistration_ShouldReturn200() {
        // Arrange
        HttpEntity<String> entity = new HttpEntity<>(null, createHeaders());

        // Act
        var restTemplate = restTemplateBuilder.build();
        ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL + VALID_REGISTRATION,
            HttpMethod.GET,
            entity,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(VALID_REGISTRATION));
    }

    @Test
    public void getVehicle_WithInvalidRegistration_ShouldReturn404() {
        // Arrange
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        var restTemplate = restTemplateBuilder.build();

        // Act & Assert
        try {
            restTemplate.exchange(
                BASE_URL + INVALID_REGISTRATION,
                HttpMethod.GET,
                entity,
                String.class
            );
            fail("Should have thrown HttpClientErrorException.NotFound");
        } catch (Exception e) {
            assertTrue(e instanceof HttpClientErrorException);
            assertEquals(HttpStatus.NOT_FOUND, ((HttpClientErrorException) e).getStatusCode());
        }
    }

    @Test
    public void getVehicle_WithoutAuthentication_ShouldReturn401() {
        // Arrange - No authentication headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        var restTemplate = restTemplateBuilder.build();

        // Act & Assert
        try {
            restTemplate.exchange(
                BASE_URL + VALID_REGISTRATION,
                HttpMethod.GET,
                entity,
                String.class
            );
            fail("Should have thrown HttpClientErrorException.Unauthorized");
        } catch (Exception e) {
            assertTrue(e instanceof HttpClientErrorException);
            assertEquals(HttpStatus.UNAUTHORIZED, ((HttpClientErrorException) e).getStatusCode());
        }
    }
}
