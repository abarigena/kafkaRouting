package com.example.commondto.dto;

import java.io.Serializable;
import java.util.Date;

public class DriverDTO implements Serializable {
    private Long id;
    private UserDTO user;
    // Дополнительные поля для водителя
    private String licenseNumber;
    private String vehicleType;
    private Date licenseExpiry;
    private Boolean isAvailable;

    // Конструкторы
    public DriverDTO() {}

    public DriverDTO(Long id, UserDTO user) {
        this.id = id;
        this.user = user;
    }

    public DriverDTO(Long id, UserDTO user, String licenseNumber,
                     String vehicleType, Date licenseExpiry, Boolean isAvailable) {
        this.id = id;
        this.user = user;
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
        this.licenseExpiry = licenseExpiry;
        this.isAvailable = isAvailable;
    }

    // Геттеры и сеттеры для всех полей
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Date getLicenseExpiry() { return licenseExpiry; }
    public void setLicenseExpiry(Date licenseExpiry) { this.licenseExpiry = licenseExpiry; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}