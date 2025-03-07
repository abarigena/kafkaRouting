package com.abarigena.orderskafka.service;

import com.example.commondto.dto.DriverDTO;
import com.example.commondto.dto.RouteResponseDTO;
import com.example.commondto.dto.SuggestionResponseDTO;
import com.example.commondto.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OrderServiceClient {
    private final RestClient restClient;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public OrderServiceClient(RestClient restClient, DiscoveryClient discoveryClient) {
        this.restClient = restClient;
        this.discoveryClient = discoveryClient;
    }

    public UserDTO getUserById(Long userId) {
        ServiceInstance serviceInstance = getServiceInstance("user-kafka");

        return restClient.get()
                .uri(serviceInstance.getUri() + "/api/users/" + userId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                    throw new RuntimeException("Пользователь или сервис не найден");
                })
                .body(UserDTO.class);
    }

    public DriverDTO getDriverById(Long driverId) {
        ServiceInstance serviceInstance = getServiceInstance("user-kafka");
        return restClient.get()
                .uri(serviceInstance.getUri() + "/api/drivers/" + driverId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                    throw new RuntimeException("Водитель или сервис не найден");
                })
                .body(DriverDTO.class);
    }

    public RouteResponseDTO getRouteById(Long routeId) {
        ServiceInstance serviceInstance = getServiceInstance("routing-kafka");
        return restClient.get()
                .uri(serviceInstance.getUri() + "/api/route/" + routeId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                    throw new RuntimeException("Маршрут или сервис не найден");
                })
                .body(RouteResponseDTO.class);
    }

    public SuggestionResponseDTO getSuggestionById(Long suggestionId) {
        ServiceInstance serviceInstance = getServiceInstance("routing-kafka");
        return restClient.get()
                .uri(serviceInstance.getUri() + "/api/suggestion/" + suggestionId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                    throw new RuntimeException("Suggestion или сервис не найден");
                })
                .body(SuggestionResponseDTO.class);
    }


    private ServiceInstance getServiceInstance(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            throw new RuntimeException("Service " + serviceName + " not available");
        }
        // Можно реализовать свою логику балансировки здесь
        return instances.get(0);
    }
}
