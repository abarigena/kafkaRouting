package com.abarigena.orderskafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OrdersKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdersKafkaApplication.class, args);
    }

}
