package com.example.commondto.dto;

public class RequestDTO {
    private Long routeId;
    private Long suggestionId;
    private boolean userConsent;
    private boolean driverConsent;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getSuggestionId() {
        return suggestionId;
    }

    public void setSuggestionId(Long suggestionId) {
        this.suggestionId = suggestionId;
    }

    public boolean isUserConsent() {
        return userConsent;
    }

    public void setUserConsent(boolean userConsent) {
        this.userConsent = userConsent;
    }

    public boolean isDriverConsent() {
        return driverConsent;
    }

    public void setDriverConsent(boolean driverConsent) {
        this.driverConsent = driverConsent;
    }

    @Override
    public String toString() {
        return "RequestDTO{" +
                "routeId=" + routeId +
                ", suggestionId=" + suggestionId +
                ", userConsent=" + userConsent +
                ", driverConsent=" + driverConsent +
                '}';
    }
}
