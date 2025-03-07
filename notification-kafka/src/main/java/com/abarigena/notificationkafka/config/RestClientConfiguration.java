package com.abarigena.notificationkafka.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    @LoadBalanced
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}
