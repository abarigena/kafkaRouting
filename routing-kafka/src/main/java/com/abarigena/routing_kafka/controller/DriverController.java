package com.abarigena.routing_kafka.controller;

import com.abarigena.routing_kafka.service.DriverService;
import com.abarigena.routing_kafka.store.entity.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/create")
    public Driver create(@RequestParam Long userId) {
        return driverService.createDriver(userId);
    }

    @GetMapping("/getAll")
    public List<Driver> getAll() {
        return driverService.findAllDrivers();
    }

    @GetMapping("/{id}")
    public Driver get(@PathVariable Long id) {
        return driverService.findDriverById(id);
    }
}
