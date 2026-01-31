package com.vtrack.dto;

import com.vtrack.entity.LogStatus;
import com.vtrack.entity.VehicleType;
import java.time.LocalDateTime;

/**
 * DTO for vehicle log response (used in GET APIs).
 */
public class VehicleLogResponse {

    private Long id;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private String driverName;
    private String purpose;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Long durationInMinutes;
    private LogStatus status;
    private boolean overstayAlert;  // true if duration > 6 hours

    public VehicleLogResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public Long getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public LogStatus getStatus() {
        return status;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    public boolean isOverstayAlert() {
        return overstayAlert;
    }

    public void setOverstayAlert(boolean overstayAlert) {
        this.overstayAlert = overstayAlert;
    }
}
