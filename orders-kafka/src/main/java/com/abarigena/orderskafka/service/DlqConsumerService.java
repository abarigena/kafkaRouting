package com.abarigena.orderskafka.service;

import com.abarigena.orderskafka.config.KafkaConfiguration;
import com.example.commondto.dto.MatchResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DlqConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(DlqConsumerService.class);

    @KafkaListener(topics = KafkaConfiguration.MATCH_RESULT_DLQ_TOPIC,
    groupId = "${spring.kafka.consumer.group-id}-dlq-consumer",
    containerFactory = "kafkaListenerContainerFactory")
    public void processDlqMessage(@Payload MatchResultDTO result,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                  @Header(KafkaHeaders.OFFSET) Long offset,
                                  @Header("idempotencyKey") String idempotencyKey,
                                  @Header("errorMetadata") Map<String, Object> errorMetadata,
                                  Acknowledgment acknowledgment){
        try {
            logger.info("Processing DLQ message from partition {}, offset {}: {}", partition, offset, result);
            logger.info("Error metadata: {}", errorMetadata);

            // Здесь можно добавить логику восстановления или специальной обработки сообщений из DLQ
            // Например, сохранение в базу данных ошибок, отправка уведомлений и т.д.

            // Можно также реализовать логику автоматического повторного запуска после определенного времени

            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing DLQ message: {}", e.getMessage(), e);
            // Подтверждаем в любом случае, чтобы избежать бесконечного цикла в DLQ
            acknowledgment.acknowledge();
        }
    }
}
