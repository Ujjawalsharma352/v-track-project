package com.vtrack.service;

import com.vtrack.dto.*;
import com.vtrack.entity.LogStatus;
import com.vtrack.entity.VehicleLog;
import com.vtrack.entity.VehicleType;
import com.vtrack.exception.BusinessException;
import com.vtrack.exception.ResourceNotFoundException;
import com.vtrack.repository.VehicleLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer - business logic for vehicle entry/exit and queries.
 * Overstay threshold: 6 hours (360 minutes).
 */
@Service
public class VehicleLogService {

    private static final long OVERSTAY_MINUTES = 6 * 60; // 6 hours

    private final VehicleLogRepository repository;

    @Value("${vtrack.admin.username:admin}")
    private String adminUsername;

    @Value("${vtrack.admin.password:admin123}")
    private String adminPassword;

    public VehicleLogService(VehicleLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Add vehicle entry. Only one active IN per vehicle allowed.
     */
    @Transactional
    public VehicleLogResponse addEntry(EntryRequest request) {
        if (repository.existsByVehicleNumberAndStatus(request.getVehicleNumber().trim(), LogStatus.IN)) {
            throw new BusinessException("Vehicle " + request.getVehicleNumber() + " is already parked (IN). Please mark exit first.");
        }
        VehicleLog log = new VehicleLog();
        log.setVehicleNumber(request.getVehicleNumber().trim());
        log.setVehicleType(request.getVehicleType());
        log.setDriverName(request.getDriverName() != null ? request.getDriverName().trim() : null);
        log.setPurpose(request.getPurpose() != null ? request.getPurpose().trim() : null);
        log.setEntryTime(LocalDateTime.now());
        log.setStatus(LogStatus.IN);
        log = repository.save(log);
        return toResponse(log);
    }

    /**
     * Mark exit for vehicle, calculate duration. Overstay if > 6 hours.
     */
    @Transactional
    public VehicleLogResponse markExit(String vehicleNumber) {
        VehicleLog log = repository.findByVehicleNumberAndStatus(vehicleNumber.trim(), LogStatus.IN)
                .orElseThrow(() -> new ResourceNotFoundException("No active entry (IN) found for vehicle: " + vehicleNumber));
        log.setExitTime(LocalDateTime.now());
        log.setDurationInMinutes(ChronoUnit.MINUTES.between(log.getEntryTime(), log.getExitTime()));
        log.setStatus(LogStatus.OUT);
        log = repository.save(log);
        VehicleLogResponse response = toResponse(log);
        response.setOverstayAlert(log.getDurationInMinutes() != null && log.getDurationInMinutes() > OVERSTAY_MINUTES);
        return response;
    }

    /**
     * Get all vehicle logs (ordered by entry time desc).
     */
    public List<VehicleLogResponse> getAllLogs() {
        return repository.findAll().stream()
                .sorted((a, b) -> b.getEntryTime().compareTo(a.getEntryTime()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get today's entries (logs where entry time is today).
     */
    public List<VehicleLogResponse> getTodayLogs() {
        return repository.findAllByEntryDate(LocalDate.now()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search by vehicle number and/or date. Date format: yyyy-MM-dd.
     */
    public List<VehicleLogResponse> search(String vehicleNumber, String dateStr) {
        boolean hasVehicle = vehicleNumber != null && !vehicleNumber.trim().isEmpty();
        boolean hasDate = dateStr != null && !dateStr.trim().isEmpty();
        LocalDate date = null;
        if (hasDate) {
            try {
                date = LocalDate.parse(dateStr.trim());
            } catch (DateTimeParseException e) {
                throw new BusinessException("Invalid date format. Use yyyy-MM-dd.");
            }
        }

        if (hasVehicle && hasDate) {
            return repository.findByVehicleNumberAndEntryDate(vehicleNumber.trim(), date).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
        if (hasVehicle) {
            return repository.findByVehicleNumberContainingIgnoreCaseOrderByEntryTimeDesc(vehicleNumber.trim()).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
        if (hasDate) {
            return repository.findByEntryDate(date).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
        return getAllLogs();
    }

    /**
     * Dashboard: today's entries, exits, currently parked.
     */
    public DashboardResponse getDashboard() {
        long todayEntries = repository.countTodayEntries();
        long todayExits = repository.countTodayExits();
        long currentlyParked = repository.countByStatus(LogStatus.IN);
        return new DashboardResponse(todayEntries, todayExits, currentlyParked);
    }

    /**
     * Most frequently visited vehicles (by exit count).
     */
    public List<MostVisitedResponse> getMostVisited() {
        return repository.findMostVisitedVehicleNumbers().stream()
                .limit(10)
                .map(row -> new MostVisitedResponse((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Export logs as CSV string.
     */
    public String exportCsv() {
        List<VehicleLog> logs = repository.findAll();
        logs.sort((a, b) -> b.getEntryTime().compareTo(a.getEntryTime()));
        StringBuilder sb = new StringBuilder();
        sb.append("id,vehicleNumber,vehicleType,driverName,purpose,entryTime,exitTime,durationInMinutes,status\n");
        for (VehicleLog log : logs) {
            sb.append(log.getId()).append(",")
                    .append(escapeCsv(log.getVehicleNumber())).append(",")
                    .append(log.getVehicleType()).append(",")
                    .append(escapeCsv(log.getDriverName())).append(",")
                    .append(escapeCsv(log.getPurpose())).append(",")
                    .append(log.getEntryTime()).append(",")
                    .append(log.getExitTime() != null ? log.getExitTime() : "").append(",")
                    .append(log.getDurationInMinutes() != null ? log.getDurationInMinutes() : "").append(",")
                    .append(log.getStatus()).append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private VehicleLogResponse toResponse(VehicleLog log) {
        VehicleLogResponse r = new VehicleLogResponse();
        r.setId(log.getId());
        r.setVehicleNumber(log.getVehicleNumber());
        r.setVehicleType(log.getVehicleType());
        r.setDriverName(log.getDriverName());
        r.setPurpose(log.getPurpose());
        r.setEntryTime(log.getEntryTime());
        r.setExitTime(log.getExitTime());
        r.setDurationInMinutes(log.getDurationInMinutes());
        r.setStatus(log.getStatus());
        boolean overstay = log.getDurationInMinutes() != null && log.getDurationInMinutes() > OVERSTAY_MINUTES;
        r.setOverstayAlert(overstay);
        return r;
    }

    /**
     * Simple admin login - validate username/password.
     */
    public boolean validateAdmin(String username, String password) {
        return adminUsername.equals(username) && adminPassword.equals(password);
    }
}
