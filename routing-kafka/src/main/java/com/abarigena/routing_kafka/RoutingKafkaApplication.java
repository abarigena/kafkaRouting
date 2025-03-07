package com.abarigena.routing_kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RoutingKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoutingKafkaApplication.class, args);
	}

}