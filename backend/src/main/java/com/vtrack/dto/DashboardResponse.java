package com.vtrack.dto;

/**
 * DTO for dashboard API (GET /api/dashboard).
 */
public class DashboardResponse {

    private long todayEntries;
    private long todayExits;
    private long currentlyParked;

    public DashboardResponse() {
    }

    public DashboardResponse(long todayEntries, long todayExits, long currentlyParked) {
        this.todayEntries = todayEntries;
        this.todayExits = todayExits;
        this.currentlyParked = currentlyParked;
    }

    public long getTodayEntries() {
        return todayEntries;
    }

    public void setTodayEntries(long todayEntries) {
        this.todayEntries = todayEntries;
    }

    public long getTodayExits() {
        return todayExits;
    }

    public void setTodayExits(long todayExits) {
        this.todayExits = todayExits;
    }

    public long getCurrentlyParked() {
        return currentlyParked;
    }

    public void setCurrentlyParked(long currentlyParked) {
        this.currentlyParked = currentlyParked;
    }
}
