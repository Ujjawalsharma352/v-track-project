CREATE DATABASE IF NOT EXISTS vtrack_db;
USE vtrack_db;

CREATE TABLE IF NOT EXISTS vehicle_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    driver_name VARCHAR(100),
    purpose VARCHAR(255),
    entry_time DATETIME NOT NULL,
    exit_time DATETIME,
    duration_in_minutes BIGINT,
    status VARCHAR(10) NOT NULL,
    INDEX idx_vehicle_status (vehicle_number, status),
    INDEX idx_entry_time (entry_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

