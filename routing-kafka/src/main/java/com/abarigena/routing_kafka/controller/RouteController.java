package com.abarigena.routing_kafka.controller;


import com.abarigena.routing_kafka.service.KafkaProducer;
import com.abarigena.routing_kafka.service.RouteService;
import com.abarigena.routing_kafka.service.SuggestionService;
import com.abarigena.routing_kafka.service.UserServiceClient;
import com.abarigena.routing_kafka.store.entity.Route;
import com.abarigena.routing_kafka.store.entity.Suggestion;
import com.example.commondto.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/route")
public class RouteController {
    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);

    private final RouteService routeService;
    private final SuggestionService suggestionService;
    private final KafkaProducer kafkaProducer;
    private final UserServiceClient userServiceClient;

    @Autowired
    public RouteController(RouteService routeService, SuggestionService suggestionService,
                           KafkaProducer kafkaProducer, UserServiceClient userServiceClient) {
        this.routeService = routeService;
        this.suggestionService = suggestionService;
        this.kafkaProducer = kafkaProducer;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping("/create")
    public Route create(@RequestBody Route route) {
        return routeService.createRoute(route);
    }

    @GetMapping("/getAll")
    public List<Route> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public RouteResponseDTO getRouteById(@PathVariable Long id) {
        Route route = routeService.getRouteById(id);
        if(route == null) {
            logger.warn("Маршрут {} не найден", id);
            throw new IllegalArgumentException("Маршрут не найден");
        }

        DriverDTO driver = userServiceClient.getDriverById(route.getDriverId());
        logger.info("Driver data: {}", driver);

        // Проверяем, что driver и driver.getUser() не равны null
        if (driver == null ) {
            logger.warn("Водитель не найден для маршрута {}", id);
            throw new IllegalArgumentException("Водитель не найден");
        }

        UserDTO user = userServiceClient.getUserById(driver.getUser().getId());

        RouteResponseDTO responseDTO = new RouteResponseDTO(route.getId(), route.getCityStart(), route.getCityEnd(),
               route.getPrice(), user, driver );

        return responseDTO;
    }

    @PostMapping("/pick")
    public NotificationRequestDTO pickRoute(@RequestParam Long routeId, @RequestParam Long userId) {
        logger.info("Процесс выбора пользователем маршрута водителя: {}, пассажир: {}", routeId, userId);

        Route route = routeService.getRouteById(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут не найден");
        }
        UserDTO user = userServiceClient.getUserById(userId);
        if(user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        Suggestion existingSuggestion = suggestionService.findExistingSuggestion(
                route.getCityStart(),
                route.getCityEnd(),
                route.getPrice(),
                userId
        );

        Suggestion suggestion;
        if (existingSuggestion != null) {
            // Если Suggestion уже существует, используем его
            suggestion = existingSuggestion;
            logger.info("Используем существующий Suggestion с ID: {}", suggestion.getId());
        } else {
            // Если Suggestion не существует, создаем новый
            suggestion = new Suggestion();
            suggestion.setUserId(userId);
            suggestion.setCityStart(route.getCityStart());
            suggestion.setCityEnd(route.getCityEnd());
            suggestion.setPrice(route.getPrice());
            suggestionService.createSuggestion(suggestion);
            logger.info("Создан новый Suggestion с ID копия Route: {}", suggestion.getId());
        }

        NotificationRequestDTO requestDTO = new NotificationRequestDTO();
        requestDTO.setRouteId(routeId);
        requestDTO.setSuggestionId(suggestion.getId());
        requestDTO.setUserId(userId);
        requestDTO.setDriverId(route.getDriverId());
        requestDTO.setRequestType(NotificationRequestDTO.RequestType.USER_INITIATED);// Пассажир всегда согласен если выбрал данный suggestion

        logger.info("Sending request to Kafka: {}", requestDTO);

        kafkaProducer.sendUserRequest(requestDTO);

        return requestDTO;
    }
}
