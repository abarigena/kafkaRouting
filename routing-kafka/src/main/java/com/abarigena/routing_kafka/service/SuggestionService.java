package com.abarigena.routing_kafka.service;

import com.abarigena.routing_kafka.store.entity.Suggestion;
import com.abarigena.routing_kafka.store.entity.User;
import com.abarigena.routing_kafka.store.repository.SuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestionService {
    private final SuggestionRepository suggestionRepository;

    @Autowired
    public SuggestionService(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    public List<Suggestion> findAll() {
        return (List<Suggestion>) suggestionRepository.findAll();
    }

    public Suggestion findById(Long id) {
        return suggestionRepository.findById(id).orElse(null);
    }

    public Suggestion createSuggestion(Suggestion suggestion) {
        return suggestionRepository.save(suggestion);
    }

    public Suggestion findExistingSuggestion(String cityStart, String cityEnd, Double price, User user) {
        return suggestionRepository.findByCityStartAndCityEndAndPriceAndUser(cityStart, cityEnd, price, user);
    }
}
