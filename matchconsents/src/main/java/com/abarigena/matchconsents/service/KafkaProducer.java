package com.abarigena.matchconsents.service;

import com.abarigena.matchconsents.config.KafkaConfiguration;
import com.abarigena.matchconsents.store.entity.MatchRequest;
import com.example.commondto.dto.MatchResultDTO;
import com.example.commondto.dto.NotificationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaProducer {
    private Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(NotificationRequestDTO requestDTO, String topic) {

        logger.info("Sending request to topic {}: {}", topic, requestDTO);

        Message<NotificationRequestDTO> message = MessageBuilder
                .withPayload(requestDTO)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("__TypeId__", NotificationRequestDTO.class.getName())
                .build();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Message sent successfully to topic: {}, partition: {}, offset: {}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message to topic {}: {}", topic, ex.getMessage());
            }
        });
    }

    public void sendMatchResult(MatchRequest request) {
        // Генерация уникального идемпотентного ключа
        String idempotencyKey = generateIdempotencyKey(request);

        MatchResultDTO matchResultDTO = new MatchResultDTO(
                request.getId(),
                request.getRouteId(),
                request.getSuggestionId(),
                request.getUserId(),
                request.getDriverId(),
                request.getUserConsent(),
                request.getDriverConsent()
        );

        logger.info("Sending match result to Kafka: {}", matchResultDTO);

        Message<MatchResultDTO> message = MessageBuilder
                .withPayload(matchResultDTO)
                .setHeader(KafkaHeaders.TOPIC, KafkaConfiguration.MATCH_RESULT_TOPIC)
                .setHeader("__TypeId__", MatchResultDTO.class.getName())
                .setHeader("idempotencyKey", idempotencyKey)
                .build();

        sendMatchWithRetry(message);
    }

    private void sendMatchWithRetry(Message<MatchResultDTO> message) {
        int maxRetries = 3;
        long initialBackoff = 1000;

        for(int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

                future.get(10, TimeUnit.SECONDS); // Ожидание ответа с таймаутом

                future.whenComplete((result,ex) ->{
                    if (ex == null) {
                        logger.info("Message sent successfully to topic: {}, partition: {}, offset: {}",
                                KafkaConfiguration.MATCH_RESULT_TOPIC,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        logger.error("Unable to send message to topic {}: {}", KafkaConfiguration.MATCH_RESULT_TOPIC, ex.getMessage());
                        throw new CompletionException(ex);
                    }
                });

                return; //Успешная отправка
            }catch (Exception e) {
                logger.warn("Attempt {} to send message failed: {}", attempt + 1, e.getMessage());

                // Exponential backoff
                try {
                    Thread.sleep(initialBackoff * (long) Math.pow(2, attempt));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // Если все попытки исчерпаны
        logger.error("Failed to send message after {} attempts", maxRetries);
    }

    private String generateIdempotencyKey(MatchRequest request) {
        return
                request.getSuggestionId() + "_" +
                request.getRouteId();
    }
}
