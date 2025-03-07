package com.abarigena.userkafka.service;

import com.abarigena.userkafka.store.entity.Driver;
import com.abarigena.userkafka.store.entity.User;
import com.abarigena.userkafka.store.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final UserService userService;

    @Autowired
    public DriverService(DriverRepository driverRepository, UserService userService) {
        this.driverRepository = driverRepository;
        this.userService = userService;
    }

    public Driver createDriver(Long id) {
        User user = userService.findById(id);
        if(user == null) {
            throw new RuntimeException("User not found");
        }

        Driver driver = new Driver();
        driver.setUser(user);
        return driverRepository.save(driver);
    }

    public List<Driver> findAllDrivers() {
        return (List<Driver>) driverRepository.findAll();
    }

    public Driver findDriverById(Long id) {
        return driverRepository.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
    }

}
