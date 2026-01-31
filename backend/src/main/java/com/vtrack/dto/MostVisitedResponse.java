package com.vtrack.dto;

/**
 * DTO for most-visited vehicle analytics.
 */
public class MostVisitedResponse {

    private String vehicleNumber;
    private long visitCount;

    public MostVisitedResponse() {
    }

    public MostVisitedResponse(String vehicleNumber, long visitCount) {
        this.vehicleNumber = vehicleNumber;
        this.visitCount = visitCount;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(long visitCount) {
        this.visitCount = visitCount;
    }
}
