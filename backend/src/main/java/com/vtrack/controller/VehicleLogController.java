package com.vtrack.controller;

import com.vtrack.dto.*;
import com.vtrack.service.VehicleLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for vehicle entry, exit, logs, search, dashboard, export.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"}, allowCredentials = "true")
public class VehicleLogController {

    private final VehicleLogService service;

    public VehicleLogController(VehicleLogService service) {
        this.service = service;
    }

    /**
     * POST /api/entry - Add vehicle entry (confirmation in response).
     */
    @PostMapping("/entry")
    public ResponseEntity<VehicleLogResponse> addEntry(@Valid @RequestBody EntryRequest request) {
        VehicleLogResponse response = service.addEntry(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/exit/{vehicleNumber} - Mark exit, calculate duration.
     */
    @PostMapping("/exit/{vehicleNumber}")
    public ResponseEntity<VehicleLogResponse> markExit(@PathVariable String vehicleNumber) {
        VehicleLogResponse response = service.markExit(vehicleNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/logs - Get all vehicle logs (protected).
     */
    @GetMapping("/logs")
    public ResponseEntity<List<VehicleLogResponse>> getAllLogs() {
        return ResponseEntity.ok(service.getAllLogs());
    }

    /**
     * GET /api/logs/today - Get today's entries (protected).
     */
    @GetMapping("/logs/today")
    public ResponseEntity<List<VehicleLogResponse>> getTodayLogs() {
        return ResponseEntity.ok(service.getTodayLogs());
    }

    /**
     * GET /api/search?vehicleNumber=&date= - Search by vehicle number or date (protected).
     * date format: yyyy-MM-dd
     */
    @GetMapping("/search")
    public ResponseEntity<List<VehicleLogResponse>> search(
            @RequestParam(required = false) String vehicleNumber,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(service.search(vehicleNumber, date));
    }

    /**
     * GET /api/dashboard - Today's entries, exits, currently parked (protected).
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(service.getDashboard());
    }

    /**
     * GET /api/analytics/most-visited - Most frequently visited vehicles (protected).
     */
    @GetMapping("/analytics/most-visited")
    public ResponseEntity<List<MostVisitedResponse>> getMostVisited() {
        return ResponseEntity.ok(service.getMostVisited());
    }

    /**
     * GET /api/export - Export logs as CSV (protected).
     */
    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<String> exportCsv() {
        String csv = service.exportCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "vtrack-logs.csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        return ResponseEntity.ok().headers(headers).body(csv);
    }
}
