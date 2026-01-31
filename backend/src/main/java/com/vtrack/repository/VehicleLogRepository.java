package com.vtrack.repository;

import com.vtrack.entity.LogStatus;
import com.vtrack.entity.VehicleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for VehicleLog entity - database access layer.
 */
@Repository
public interface VehicleLogRepository extends JpaRepository<VehicleLog, Long> {

    /**
     * Find active (IN) log for a vehicle - only one allowed per vehicle.
     */
    Optional<VehicleLog> findByVehicleNumberAndStatus(String vehicleNumber, LogStatus status);

    /**
     * Check if vehicle has an active IN entry.
     */
    boolean existsByVehicleNumberAndStatus(String vehicleNumber, LogStatus status);

    /**
     * Find all logs where entry time is on the given date.
     */
    @Query("SELECT v FROM VehicleLog v WHERE DATE(v.entryTime) = :date ORDER BY v.entryTime DESC")
    List<VehicleLog> findAllByEntryDate(@Param("date") LocalDate date);

    /**
     * Search by vehicle number (partial match).
     */
    List<VehicleLog> findByVehicleNumberContainingIgnoreCaseOrderByEntryTimeDesc(String vehicleNumber);

    /**
     * Search by entry date.
     */
    @Query("SELECT v FROM VehicleLog v WHERE DATE(v.entryTime) = :date ORDER BY v.entryTime DESC")
    List<VehicleLog> findByEntryDate(@Param("date") LocalDate date);

    /**
     * Search by vehicle number and date.
     */
    @Query("SELECT v FROM VehicleLog v WHERE LOWER(v.vehicleNumber) LIKE LOWER(CONCAT('%', :vehicleNumber, '%')) AND DATE(v.entryTime) = :date ORDER BY v.entryTime DESC")
    List<VehicleLog> findByVehicleNumberAndEntryDate(@Param("vehicleNumber") String vehicleNumber, @Param("date") LocalDate date);

    /**
     * Count today's entries (where entry time is today).
     */
    @Query("SELECT COUNT(v) FROM VehicleLog v WHERE DATE(v.entryTime) = CURRENT_DATE")
    long countTodayEntries();

    /**
     * Count today's exits (where exit time is today).
     */
    @Query("SELECT COUNT(v) FROM VehicleLog v WHERE v.status = 'OUT' AND DATE(v.exitTime) = CURRENT_DATE")
    long countTodayExits();

    /**
     * Count currently parked vehicles (status = IN).
     */
    long countByStatus(LogStatus status);

    /**
     * Find most frequently visited vehicles (count of OUT logs per vehicle number).
     */
    @Query("SELECT v.vehicleNumber, COUNT(v) FROM VehicleLog v WHERE v.status = 'OUT' GROUP BY v.vehicleNumber ORDER BY COUNT(v) DESC")
    List<Object[]> findMostVisitedVehicleNumbers();
}
