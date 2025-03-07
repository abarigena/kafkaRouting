package com.abarigena.routing_kafka.service;

import com.abarigena.routing_kafka.store.entity.Route;
import com.abarigena.routing_kafka.store.repository.RouteRepository;
import com.example.commondto.dto.DriverDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final Logger logger = LoggerFactory.getLogger(RouteService.class);

    private final RouteRepository routeRepository;
    private final UserServiceClient userServiceClient;

    @Autowired
    public RouteService(RouteRepository routeRepository, UserServiceClient userServiceClient) {
        this.routeRepository = routeRepository;
        this.userServiceClient = userServiceClient;
    }

    public Route createRoute(Route route) {
        Long driverId = route.getDriverId();
        try {
            DriverDTO driverDTO = userServiceClient.getDriverById(driverId);
            logger.info("driver found: {}", driverDTO);
        }catch (RuntimeException e) {
            throw new IllegalArgumentException("Driver with ID " + driverId + " not found");
        }

        return routeRepository.save(route);
    }

    public List<Route> getAllRoutes() {
        return (List<Route>) routeRepository.findAll();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id).orElse(null);
    }

    public Route findExistRoute(String cityStart, String cityEnd, Double price, Long driverId) {
        return routeRepository.findByCityStartAndCityEndAndPriceAndDriverId(cityStart,cityEnd,price,driverId);
    }
}
