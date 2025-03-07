package com.abarigena.routing_kafka.service;

import com.abarigena.routing_kafka.store.entity.Suggestion;
import com.abarigena.routing_kafka.store.repository.SuggestionRepository;
import com.example.commondto.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestionService {
    private final Logger logger = LoggerFactory.getLogger(SuggestionService.class);

    private final SuggestionRepository suggestionRepository;
    private final UserServiceClient userServiceClient;

    @Autowired
    public SuggestionService(SuggestionRepository suggestionRepository, UserServiceClient userServiceClient) {
        this.suggestionRepository = suggestionRepository;
        this.userServiceClient = userServiceClient;
    }

    public List<Suggestion> findAll() {
        return (List<Suggestion>) suggestionRepository.findAll();
    }

    public Suggestion findById(Long id) {
        return suggestionRepository.findById(id).orElse(null);
    }

    public Suggestion createSuggestion(Suggestion suggestion) {
        Long userId = suggestion.getUserId();
        try{
            UserDTO userDTO = userServiceClient.getUserById(userId);
            logger.info("user found: {}", userDTO);
        }catch (RuntimeException e){
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
        return suggestionRepository.save(suggestion);
    }

    public Suggestion findExistingSuggestion(String cityStart, String cityEnd, Double price, Long userId) {
        return suggestionRepository.findByCityStartAndCityEndAndPriceAndUserId(cityStart, cityEnd, price, userId);
    }
}
