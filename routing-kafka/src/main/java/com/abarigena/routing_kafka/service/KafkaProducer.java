package com.abarigena.routing_kafka.service;

import com.abarigena.routing_kafka.config.KafkaConfiguration;
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

@Service
public class KafkaProducer {
    private Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, NotificationRequestDTO> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, NotificationRequestDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserRequest(NotificationRequestDTO requestDTO) {
        sendMessage(requestDTO, KafkaConfiguration.USER_REQUEST_TOPIC);
    }

    public void sendDriverRequest(NotificationRequestDTO requestDTO) {
        sendMessage(requestDTO, KafkaConfiguration.DRIVER_REQUEST_TOPIC);
    }

    public void sendMessage(NotificationRequestDTO requestDTO, String topic) {

        logger.info("Sending request to topic {}: {}", topic, requestDTO);

        Message<NotificationRequestDTO> message = MessageBuilder
                .withPayload(requestDTO)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("__TypeId__", NotificationRequestDTO.class.getName())
                .build();

        CompletableFuture<SendResult<String, NotificationRequestDTO>> future = kafkaTemplate.send(message);

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
}
