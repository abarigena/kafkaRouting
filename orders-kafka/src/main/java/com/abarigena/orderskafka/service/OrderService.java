package com.abarigena.orderskafka.service;

import com.abarigena.orderskafka.config.KafkaConfiguration;
import com.abarigena.orderskafka.store.entity.Order;
import com.abarigena.orderskafka.store.repository.OrderRepository;
import com.example.commondto.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderServiceClient orderServiceClient;
    private final DlqService dlqService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderServiceClient orderServiceClient,
                        DlqService dlqService) {
        this.orderRepository = orderRepository;
        this.orderServiceClient = orderServiceClient;
        this.dlqService = dlqService;
    }

    @KafkaListener(topics = KafkaConfiguration.MATCH_RESULT_TOPIC, groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void processMatchResult(@Payload MatchResultDTO result,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                   @Header(KafkaHeaders.OFFSET) Long offset,
                                   @Header("idempotencyKey") String idempotencyKey,
                                   Acknowledgment acknowledgment){
        logger.info("Received request from partition {}, offset {}: {}", partition, offset, result);

        try{
            validateMatchResult(result);

            // Проверяем существование заказа с таким идемпотентным ключом
            Optional<Order> existingOrder = orderRepository.findByIdempotencyKey(idempotencyKey);

            if (existingOrder.isPresent()) {
                logger.info("Order with idempotency key {} already exists. Skipping.", idempotencyKey);
                acknowledgment.acknowledge(); // Подтверждаем обработку
                dlqService.clearRetryCount(idempotencyKey);
                return;
            }
            if (Boolean.TRUE.equals(result.getIsMatched())) {
                try {
                    Order order = createOrderFromMatchResult(result, idempotencyKey);
                    orderRepository.save(order);
                    logger.info("Order created successfully: {}", order);
                    logger.info("Order created successfully: {}", order);
                    acknowledgment.acknowledge(); // Подтверждаем обработку
                    dlqService.clearRetryCount(idempotencyKey); // Очищаем счетчик повторных попыток
                } catch (Exception e) {
                    logger.error("Error creating order from match result: {}", result, e);
                    // Отправляем в DLQ при ошибке создания заказа
                    dlqService.handleFailedMessage(result, idempotencyKey, e);
                    throw e; // Пробрасываем исключение для отмены транзакции
                }
            } else {
                logger.info("Skipping order creation - match not confirmed: {}", result);
                acknowledgment.acknowledge(); // Подтверждаем обработку
                dlqService.clearRetryCount(idempotencyKey); // Очищаем счетчик повторных попыток
            }

        }catch (Exception e){
            logger.error("Error processing request: {}", e.getMessage(), e);
            // Отправляем в DLQ при общей ошибке обработки
            dlqService.handleFailedMessage(result, idempotencyKey, e);
            // Не вызываем acknowledgment.acknowledge() здесь, чтобы сообщение могло быть обработано повторно
        }
    }

    private void validateMatchResult(MatchResultDTO result) {
        if (result == null) {
            throw new IllegalArgumentException("MatchResultDTO cannot be null");
        }
        if (result.getRouteId() == null) {
            throw new IllegalArgumentException("RouteId cannot be null");
        }
        if (result.getSuggestionId() == null) {
            throw new IllegalArgumentException("SuggestionId cannot be null");
        }
        if (result.getUserId() == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (result.getDriverId() == null) {
            throw new IllegalArgumentException("DriverId cannot be null");
        }
        if (result.getIsMatched() == null) {
            throw new IllegalArgumentException("IsMatched flag cannot be null");
        }
    }

    private Order createOrderFromMatchResult(MatchResultDTO matchResultDTO, String idempotencyKey) {
        Order order = new Order();
        order.setRouteId(matchResultDTO.getRouteId());
        order.setSuggestionId(matchResultDTO.getSuggestionId());
        order.setUserId(matchResultDTO.getUserId());
        order.setDriverId(matchResultDTO.getDriverId());
        order.setIdempotencyKey(idempotencyKey);
        return order;
    }

    public List<Order> findAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }

    public List<Order> findAllOrderByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    public List<Order> findAllOrderByDriverId(Long driverId) {
        return orderRepository.findAllByDriverId(driverId);
    }

    public OrderDTO getOrderById(Long id) {
        // Проверка входящего параметра
        if (id == null) {
            logger.error("Attempt to retrieve order with null ID");
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        // Находим заказ в репозитории
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", id);
                    return new EntityNotFoundException("Order not found with ID: " + id);
                });
        try {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(id);

            // Параллельное получение данных для повышения производительности
            CompletableFuture<DriverDTO> driverFuture = CompletableFuture.supplyAsync(() ->
                    orderServiceClient.getDriverById(order.getDriverId())
            );

            CompletableFuture<RouteResponseDTO> routeFuture = CompletableFuture.supplyAsync(() ->
                    orderServiceClient.getRouteById(order.getRouteId())
            );

            CompletableFuture<SuggestionResponseDTO> suggestionFuture = CompletableFuture.supplyAsync(() ->
                    orderServiceClient.getSuggestionById(order.getSuggestionId())
            );

            CompletableFuture<List<UserDTO>> usersFuture = CompletableFuture.supplyAsync(() ->
                    Collections.singletonList(orderServiceClient.getUserById(order.getUserId()))
            );

            // Ожидаем завершения всех асинхронных вызовов
            CompletableFuture.allOf(driverFuture, routeFuture, suggestionFuture, usersFuture).join();

            // Устанавливаем полученные данные
            orderDTO.setDriver(driverFuture.get());
            orderDTO.setRoute(routeFuture.get());
            orderDTO.setSuggestion(suggestionFuture.get());
            orderDTO.setUsers(usersFuture.get());

            logger.info("Successfully retrieved order details for ID: {}", id);
            return orderDTO;

        } catch (Exception e) {
            logger.error("Error retrieving order details for ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve full order details", e);
        }
    }
}
