package com.abarigena.orderskafka.service;

import com.abarigena.orderskafka.config.KafkaConfiguration;
import com.example.commondto.dto.MatchResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DlqService {
    private static final Logger logger = LoggerFactory.getLogger(DlqService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, Integer> retryCount = new ConcurrentHashMap<>();

    @Autowired
    public DlqService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void handleFailedMessage(MatchResultDTO payload, String idempotencyKey, Exception exception) {
        // Увеличиваем счетчик повторных попыток
        int currentRetries = retryCount.getOrDefault(idempotencyKey, 0) +1;
        retryCount.put(idempotencyKey, currentRetries);

        // Если превышено максимальное количество попыток, отправляем в DLQ
        if(currentRetries >= KafkaConfiguration.MAX_RETRIES) {
            logger.warn("Moving message to DLQ after {} failed attempts: {}", currentRetries, payload);
            sendToDlq(payload, idempotencyKey, exception);
            retryCount.remove(idempotencyKey); // Очищаем счетчик после отправки в DLQ
        }else {
            logger.info("Will retry processing message later (attempt {}/{}): {}",
                    currentRetries, KafkaConfiguration.MAX_RETRIES, payload);
        }
    }

    private void sendToDlq(MatchResultDTO payload, String idempotencyKey, Exception exception) {
        try {
            // Создаем метаданные с дополнительной информацией об ошибке
            Map<String, Object> errorMetadata = new HashMap<>();
            errorMetadata.put("errorMessage", exception.getMessage());
            errorMetadata.put("errorType", exception.getClass().getName());
            errorMetadata.put("timestamp", System.currentTimeMillis());

            // Создаем сообщение для DLQ
            Message<MatchResultDTO> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, KafkaConfiguration.MATCH_RESULT_DLQ_TOPIC)
                    .setHeader("idempotencyKey", idempotencyKey)
                    .setHeader("errorMetadata", errorMetadata)
                    .build();

            // Отправляем в DLQ
            kafkaTemplate.send(message);
            logger.info("Successfully sent message to DLQ: {}", payload);
        } catch (Exception e) {
            logger.error("Failed to send message to DLQ: {}", e.getMessage(), e);
        }
    }

    public void clearRetryCount(String idempotencyKey) {
        retryCount.remove(idempotencyKey);
    }
}
