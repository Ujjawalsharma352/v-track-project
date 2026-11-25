package com.vtrack.controller;

import com.vtrack.dto.LoginRequest;
import com.vtrack.dto.LoginResponse;
import com.vtrack.service.VehicleLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin login API - returns token for protected endpoints.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final VehicleLogService vehicleLogService;

    @Value("${vtrack.auth.token}")
    private String authToken;

    public AuthController(VehicleLogService vehicleLogService) {
        this.vehicleLogService = vehicleLogService;
    }

    /**
     * POST /api/login
     * Body: configured admin username and password.
     * Returns token to use in X-Auth-Token or Authorization: Bearer &lt;token&gt;
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        if (vehicleLogService.validateAdmin(request.getUsername(), request.getPassword())) {
            return ResponseEntity.ok(new LoginResponse(true, "Login successful", authToken));
        }
        return ResponseEntity.status(401).body(new LoginResponse(false, "Invalid username or password", null));
    }
}
