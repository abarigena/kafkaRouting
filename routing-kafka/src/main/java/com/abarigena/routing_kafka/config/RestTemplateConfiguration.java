package com.abarigena.routing_kafka.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    @LoadBalanced
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}
