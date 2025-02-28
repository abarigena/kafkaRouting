package com.abarigena.request_kafka.service;

import com.abarigena.request_kafka.store.entity.Request;
import com.abarigena.request_kafka.store.repository.RequestRepository;
import com.example.commondto.dto.RequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private RequestRepository requestRepository;

    @KafkaListener(topics = "request", groupId = "req_consumer", containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload RequestDTO requestDTO,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset,
                       Acknowledgment acknowledgment) {
        logger.info("Received request from partition {}, offset {}: {}", partition, offset, requestDTO);

        try{

            Request request = convertToEntity(requestDTO);

            Request savedRequest = requestRepository.save(request);
            logger.info("Saved request with ID: {}", savedRequest.getId());

            // Если оба согласны, отправляем в сервис заказов
            if (requestDTO.isUserConsent() && requestDTO.isDriverConsent()) {
                // Проверяем, был ли такой запрос с согласием обоих уже сохранен
                if (!isDuplicateAcceptedRequest(request)) {
                    logger.info("Both user and driver consented. Preparing to send to order service.");
                    // Здесь будет логика для отправки в сервис заказов
                    // orderService.createOrder(savedRequest);
                } else {
                    logger.info("Duplicate accepted request detected. Skipping order creation.");
                }
            }

            // Подтверждаем обработку сообщения
            acknowledgment.acknowledge();
            logger.info("Message processing acknowledged for partition {}, offset {}", partition, offset);

        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            // Не делаем acknowledgment, что приведет к повторной обработке
            // Можно добавить логику для ограничения попыток или перемещения в DLQ
        }
    }

    private Request convertToEntity(RequestDTO dto) {
        Request request = new Request();
        request.setRouteId(dto.getRouteId());
        request.setSuggestionId(dto.getSuggestionId());
        request.setUserConsent(dto.isUserConsent());
        request.setDriverConsent(dto.isDriverConsent());
        return request;
    }

    private boolean isDuplicateAcceptedRequest(Request request) {
        // Проверка на наличие дубликатов с согласием обоих
        return requestRepository.existsByRouteIdAndSuggestionIdAndUserConsentIsTrueAndDriverConsentIsTrue(
                request.getRouteId(), request.getSuggestionId());
    }
}
