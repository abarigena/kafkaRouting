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
@RequestMapping("/api/suggestion")
public class SuggestionController {
    private static final Logger logger = LoggerFactory.getLogger(SuggestionController.class);

    private final SuggestionService suggestionService;
    private final RouteService routeService;
    private final KafkaProducer kafkaProducer;
    private final UserServiceClient userServiceClient;

    @Autowired
    public SuggestionController(SuggestionService suggestionService, RouteService routeService,
                                KafkaProducer kafkaProducer, UserServiceClient userServiceClient) {
        this.suggestionService = suggestionService;
        this.routeService = routeService;
        this.kafkaProducer = kafkaProducer;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping("/create")
    public Suggestion create(@RequestBody Suggestion suggestion) {
        logger.info("Создание нового Suggestion от пользователя: {}", suggestion);
        return suggestionService.createSuggestion(suggestion);
    }

    @GetMapping("/getAll")
    public List<Suggestion> getAll() {
        logger.info("Поиск всех Suggestions");
        return suggestionService.findAll();
    }

    @GetMapping("/{id}")
    public SuggestionResponseDTO getSuggestionById(@PathVariable Long id) {

        Suggestion suggestion = suggestionService.findById(id);
        if(suggestion == null) {
            logger.warn("Suggestion {} не найден", id);
            throw new IllegalArgumentException("Suggestion " + id + " не найден");
        }

        UserDTO user = userServiceClient.getUserById(suggestion.getUserId());
        if(user == null) {
            logger.warn("Пользователь не найден для маршрута {}", id);
            throw new IllegalArgumentException("Пользователь не найден");
        }

        SuggestionResponseDTO suggestionResponseDTO = new SuggestionResponseDTO(suggestion.getId(), suggestion.getCityStart(),
                suggestion.getCityEnd(), suggestion.getPrice(), user);
        return suggestionResponseDTO;
    }

    @PostMapping("/pick")
    public NotificationRequestDTO pick(@RequestParam Long suggestionId, @RequestParam Long driverId) {
        logger.info("Процесс выбора пользователем маршрута водителя: {}, пассажир: {}", suggestionId, driverId);

        Suggestion suggestion = suggestionService.findById(suggestionId);
        if(suggestion == null) {
            logger.warn("Suggestions {} не найден", suggestionId);
            throw new IllegalArgumentException("Suggestion не найден");
        }

        DriverDTO driverDTO = userServiceClient.getDriverById(driverId);
        if(driverDTO == null) {
            logger.warn("Driver {} not found", driverId);
            throw new IllegalArgumentException("Driver not found");
        }

        Route existingRoute = routeService.findExistRoute(
                suggestion.getCityStart(),
                suggestion.getCityEnd(),
                suggestion.getPrice(),
                driverId
        );

        Route route;
        if(existingRoute != null) {
            // используем существующий route
            route = existingRoute;
            logger.info("Используем существующий route c ID: {}", route.getId());
        }else {
            // Создаем новый роут
            route = new Route();
            route.setDriverId(driverId);
            route.setCityStart(suggestion.getCityStart());
            route.setCityEnd(suggestion.getCityEnd());
            route.setPrice(suggestion.getPrice());
            route = routeService.createRoute(route);
            logger.info("Создаем новый роут с ID: {}", route.getId());
        }

        // Создаем и отправляем RequestDTO
        NotificationRequestDTO requestDTO = new NotificationRequestDTO();
        requestDTO.setRouteId(route.getId());
        requestDTO.setSuggestionId(suggestionId);
        requestDTO.setUserId(suggestion.getUserId());
        requestDTO.setDriverId(driverId);
        requestDTO.setRequestType(NotificationRequestDTO.RequestType.DRIVER_INITIATED);// Водитель всегда согласен если выбрал данный suggestion

        logger.info("Sending request to Kafka: {}", requestDTO);
        kafkaProducer.sendDriverRequest(requestDTO);

        return requestDTO;
    }
}
