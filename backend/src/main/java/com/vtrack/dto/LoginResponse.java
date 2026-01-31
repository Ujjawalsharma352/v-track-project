package com.vtrack.dto;

/**
 * DTO for login response (returns token/success for frontend).
 */
public class LoginResponse {

    private boolean success;
    private String message;
    private String token;  // Simple token for demo; frontend stores and sends in header

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
