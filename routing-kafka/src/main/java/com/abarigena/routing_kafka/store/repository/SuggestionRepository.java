package com.abarigena.routing_kafka.store.repository;

import com.abarigena.routing_kafka.store.entity.Suggestion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionRepository extends CrudRepository<Suggestion, Long> {
    Suggestion findByCityStartAndCityEndAndPriceAndUserId(String cityStart, String cityEnd, Double price, Long userId);
}
