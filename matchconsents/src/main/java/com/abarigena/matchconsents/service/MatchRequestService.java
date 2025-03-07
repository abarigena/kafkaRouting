package com.abarigena.matchconsents.service;

import com.abarigena.matchconsents.config.KafkaConfiguration;
import com.abarigena.matchconsents.store.entity.MatchRequest;
import com.abarigena.matchconsents.store.repository.MatchRequestRepository;
import com.example.commondto.dto.NotificationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MatchRequestService {
    private static final Logger logger = LoggerFactory.getLogger(MatchRequestService.class);

    private final MatchRequestRepository matchRequestRepository;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public MatchRequestService(MatchRequestRepository matchRequestRepository, KafkaProducer kafkaProducer) {
        this.matchRequestRepository = matchRequestRepository;
        this.kafkaProducer = kafkaProducer;
    }

    // Обработка входящих запросов от пользователя или водителя
    @KafkaListener(topics = {
            KafkaConfiguration.USER_REQUEST_TOPIC,
            KafkaConfiguration.DRIVER_REQUEST_TOPIC
    },groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void processRequest(@Payload NotificationRequestDTO requestDTO,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                               @Header(KafkaHeaders.OFFSET) Long offset,
                               Acknowledgment acknowledgment) {
        logger.info("Received request from partition {}, offset {}: {}", partition, offset, requestDTO);

        try{
            // Создаем запись о запросе в БД
            MatchRequest request = new MatchRequest();
            request.setRouteId(requestDTO.getRouteId());
            request.setSuggestionId(requestDTO.getSuggestionId());
            request.setUserId(requestDTO.getUserId());
            request.setDriverId(requestDTO.getDriverId());
            request.setRequestType(requestDTO.getRequestType().toString());

            // Устанавливаем начальные значения согласий в зависимости от типа запроса
            if(requestDTO.getRequestType() == NotificationRequestDTO.RequestType.USER_INITIATED){
                // Если запрос от пользователя, устанавливаем его согласие как true
                request.setUserConsent(true);
                request.setDriverConsent(null);
            }else {
                request.setDriverConsent(true);
                request.setUserConsent(null);
            }

            request = matchRequestRepository.save(request);

            requestDTO.setRequestId(request.getId());

            kafkaProducer.sendNotification(requestDTO,KafkaConfiguration.NOTIFICATION_REQUEST_TOPIC);

            // Подтверждаем успешную обработку сообщения
            acknowledgment.acknowledge();
        } catch (Exception e){
            logger.error("Error processing request: {}", e.getMessage(), e);
            // Можно добавить логику для ограничения попыток или перемещения в DLQ
        }
    }

    // Обработка согласия от пользователя
    @Transactional
    public void processUserConsent(Long notificationRequestId, Long userId, Boolean userConsent){
        Optional<MatchRequest> reequestOpt = matchRequestRepository.findById(notificationRequestId);
        if(!reequestOpt.isPresent()){
            logger.error("Request not found: {}", notificationRequestId);
            throw new IllegalArgumentException("Request not found");
        }

        MatchRequest request = reequestOpt.get();

        if(!request.getUserId().equals(userId)){
            logger.error("User ID mismatch: {} vs {}", userId, request.getUserId());
            throw new IllegalArgumentException("User ID mismatch");
        }

        request.setUserConsent(userConsent);
        request = matchRequestRepository.save(request);

        // Проверяем, получены ли оба ответа, и отправляем результат в Kafka
        if(request.getUserConsent()!=null && request.getDriverConsent()!=null){
            kafkaProducer.sendMatchResult(request);
        }
    }

    // Обработка согласия от водителя
    @Transactional
    public void processDriverConsent(Long notificationRequestId, Long driverId, Boolean driverConsent){
        Optional<MatchRequest> reequestOpt = matchRequestRepository.findById(notificationRequestId);
        if(!reequestOpt.isPresent()){
            logger.error("Request not found: {}", notificationRequestId);
            throw new IllegalArgumentException("Request not found");
        }

        MatchRequest request = reequestOpt.get();

        if(!request.getDriverId().equals(driverId)){
            logger.error("Driver ID mismatch: {} vs {}", driverId, request.getDriverId());
            throw new IllegalArgumentException("Driver ID mismatch");
        }

        request.setDriverConsent(driverConsent);
        request = matchRequestRepository.save(request);

        // Проверяем, получены ли оба ответа, и отправляем результат в Kafka
        if(request.getUserConsent()!=null && request.getDriverConsent()!=null){
            kafkaProducer.sendMatchResult(request);
        }
    }

    public MatchRequest getRequestStatus(Long requestId) {
        Optional<MatchRequest> requestOpt = matchRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            return requestOpt.get();
        }
        logger.error("Request not found: {}", requestId);
        throw new IllegalArgumentException("Request not found");
    }
}
