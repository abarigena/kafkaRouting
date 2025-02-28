package com.abarigena.routing_kafka.config;

import com.example.commondto.dto.RequestDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, RequestDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put("spring.json.add.type.headers", true);

        // Дополнительные настройки для надежности
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Подтверждение от всех реплик
        configProps.put(ProducerConfig.RETRIES_CONFIG, 10); // Количество повторов при ошибке
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000); // Интервал между повторами (мс)

        // Оптимизация производительности (опционально)
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // Размер батча
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10); // Задержка отправки для группировки сообщений
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // Размер буфера памяти (32MB)

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, RequestDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic routingTopic() {
        return TopicBuilder.name("request").build();
    }
}
