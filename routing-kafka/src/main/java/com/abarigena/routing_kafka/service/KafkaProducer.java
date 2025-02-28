package com.abarigena.routing_kafka.service;

import com.example.commondto.dto.RequestDTO;
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

    private final KafkaTemplate<String, RequestDTO> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, RequestDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(RequestDTO requestDTO) {

        logger.info("Sending request to {}", requestDTO);

        Message<RequestDTO> message = MessageBuilder
                .withPayload(requestDTO)
                .setHeader(KafkaHeaders.TOPIC, "request")
                .setHeader("__TypeId__", RequestDTO.class.getName())
                .build();

        CompletableFuture<SendResult<String, RequestDTO>> future = kafkaTemplate.send(message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Message sent successfully to topic: request, partition: {}, offset: {}",
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }else {
                logger.error("Unable to send message: {}", ex.getMessage());
            }
        });
    }
}
