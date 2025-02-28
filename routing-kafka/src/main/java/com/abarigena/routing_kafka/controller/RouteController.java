package com.abarigena.routing_kafka.controller;


import com.abarigena.routing_kafka.service.KafkaProducer;
import com.abarigena.routing_kafka.service.RouteService;
import com.abarigena.routing_kafka.service.SuggestionService;
import com.abarigena.routing_kafka.service.UserService;
import com.abarigena.routing_kafka.store.entity.Route;
import com.abarigena.routing_kafka.store.entity.Suggestion;
import com.abarigena.routing_kafka.store.entity.User;
import com.example.commondto.dto.RequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/route")
public class RouteController {
    private Logger logger = LoggerFactory.getLogger(RouteController.class);

    private final RouteService routeService;
    private final UserService userService;
    private final SuggestionService suggestionService;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public RouteController(RouteService routeService, UserService userService, SuggestionService suggestionService, KafkaProducer kafkaProducer) {
        this.routeService = routeService;
        this.userService = userService;
        this.suggestionService = suggestionService;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/create")
    public Route create(@RequestBody Route route) {
        return routeService.creteRoute(route);
    }

    @GetMapping("/getAll")
    public List<Route> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public Route getRouteById(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    @PostMapping("/pick")
    public RequestDTO pickRoute(@RequestParam Long routeId, @RequestParam Long userId) {
        Route route = routeService.getRouteById(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут не найден");
        }
        User user = userService.findById(userId);

        Suggestion existingSuggestion = suggestionService.findExistingSuggestion(
                route.getCityStart(),
                route.getCityEnd(),
                route.getPrice(),
                user
        );

        Suggestion suggestion;
        if (existingSuggestion != null) {
            // Если Suggestion уже существует, используем его
            suggestion = existingSuggestion;
            logger.info("Используем существующий Suggestion с ID: {}", suggestion.getId());
        } else {
            // Если Suggestion не существует, создаем новый
            suggestion = new Suggestion();
            suggestion.setUser(user);
            suggestion.setCityStart(route.getCityStart());
            suggestion.setCityEnd(route.getCityEnd());
            suggestion.setPrice(route.getPrice());
            suggestionService.createSuggestion(suggestion);
            logger.info("Создан новый Suggestion с ID: {}", suggestion.getId());
        }

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRouteId(routeId);
        requestDTO.setSuggestionId(suggestion.getId());
        requestDTO.setUserConsent(true);

        boolean driverConsent = new Random().nextBoolean();
        requestDTO.setDriverConsent(driverConsent);

        kafkaProducer.send(requestDTO);

        return requestDTO;
    }
}
