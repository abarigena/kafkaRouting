package com.abarigena.notificationkafka.service;

import com.abarigena.notificationkafka.config.KafkaConfiguration;
import com.abarigena.notificationkafka.store.entity.Notification;
import com.abarigena.notificationkafka.store.repository.NotificationRepository;
import com.example.commondto.dto.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RouteServiceClient routeServiceClient;

    @Autowired
    public NotificationService(
            NotificationRepository notificationRepository,
            KafkaTemplate<String, Object> kafkaTemplate, RouteServiceClient routeServiceClient) {
        this.notificationRepository = notificationRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.routeServiceClient = routeServiceClient;
    }

    // Обработка входящих запросов от пользователя или водителя
    @KafkaListener(topics = KafkaConfiguration.NOTIFICATION_REQUEST_TOPIC
            ,groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void processRequest(@Payload NotificationRequestDTO requestDTO,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                               @Header(KafkaHeaders.OFFSET) Long offset,
                               Acknowledgment acknowledgment) {
        logger.info("Received request from partition {}, offset {}: {}", partition, offset, requestDTO);

        try{
            // Отправляем уведомления соответствующим участникам
            if (requestDTO.getRequestType() == NotificationRequestDTO.RequestType.USER_INITIATED){
                // Если запрос от пользователя, отправляем уведомление водителю
                createAndSendNotification(requestDTO.getRequestId(), requestDTO.getDriverId(), "DRIVER",
                        generateDriverNotificationContent(requestDTO));
            } else {
                // Если запрос от водителя, отправляем уведомление пользователю
                createAndSendNotification(requestDTO.getRequestId(), requestDTO.getUserId(), "USER",
                        generateUserNotificationContent(requestDTO));
            }

            // Подтверждаем успешную обработку сообщения
            acknowledgment.acknowledge();
        } catch (Exception e){
            logger.error("Error processing request: {}", e.getMessage(), e);
            // Можно добавить логику для ограничения попыток или перемещения в DLQ
        }
    }

    // Создание и отправка уведомления
    private void createAndSendNotification(Long requestId, Long recipientId, String recipientType, String content){
        Notification notification = new Notification();
        notification.setRequestId(requestId);
        notification.setRecipientId(recipientId);
        notification.setRecipientType(recipientType);
        notification.setContent(content);

        notification = notificationRepository.save(notification);
        logger.info("Created notification: {}", notification);

        // Здесь можно добавить реальную отправку уведомлений через
        // Push-сервис, Email, SMS и т.д.

        // Для демонстрации просто отмечаем как отправленное

        notification.markAsSent();
        notificationRepository.save(notification);
    }

    // Генерация содержимого уведомления для пользователя
    private String generateUserNotificationContent(NotificationRequestDTO request) {
        RouteResponseDTO routeResponseDTO = routeServiceClient.getRouteById(request.getRouteId());
        DriverDTO driver = routeServiceClient.getDriverById(request.getDriverId());
        return String.format(
                "Водитель %s предложил вам маршрут из %s в %s за %.2f руб. Хотите принять предложение?",
                driver.getUser().getName(),
                routeResponseDTO.getCityStart(), routeResponseDTO.getCityEnd(), routeResponseDTO.getPrice());
    }

    // Генерация содержимого уведомления для водителя
    private String generateDriverNotificationContent(NotificationRequestDTO request) {
        RouteResponseDTO routeResponseDTO = routeServiceClient.getRouteById(request.getRouteId());
        UserDTO user = routeServiceClient.getUserById(request.getUserId());
        return String.format(
                "Пассажир %s хочет поехать по вашему маршруту из %s в %s за %.2f. Вы согласны взять пассажира?",
                user.getName(),
                routeResponseDTO.getCityStart(), routeResponseDTO.getCityEnd(), routeResponseDTO.getPrice());
    }

    // Получение активных уведомлений для пользователя
    public List<Notification> getActiveNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientIdAndRecipientTypeAndIsRead(userId, "USER", false);
    }

    // Получение активных уведомлений для водителя
    public List<Notification> getActiveNotificationsForDriver(Long driverId) {
        return notificationRepository.findByRecipientIdAndRecipientTypeAndIsRead(driverId, "DRIVER", false);
    }

    // Пометить уведомление как прочитанное
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.markAsRead();
            notificationRepository.save(notification);
        } else {
            logger.error("Notification not found: {}", notificationId);
            throw new IllegalArgumentException("Notification not found");
        }
    }

    // Получение истории уведомлений пользователя
    public List<Notification> getUserNotificationHistory(Long userId) {
        return notificationRepository.findByRecipientIdAndRecipientType(userId, "USER");
    }

    // Получение истории уведомлений водителя
    public List<Notification> getDriverNotificationHistory(Long driverId) {
        return notificationRepository.findByRecipientIdAndRecipientType(driverId, "DRIVER");
    }
}
