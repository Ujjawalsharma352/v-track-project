package com.vtrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a vehicle entry/exit log.
 * Stored in MySQL table vehicle_log.
 */
@Entity
@Table(name = "vehicle_log")
public class VehicleLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleType vehicleType;

    @Column(length = 100)
    private String driverName;

    @Column(length = 255)
    private String purpose;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    @Column
    private LocalDateTime exitTime;

    @Column
    private Long durationInMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LogStatus status;

    // Default constructor (required by JPA)
    public VehicleLog() {
    }

    // Getters and Setters
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
}
