package com.abarigena.routing_kafka.store.repository;

import com.abarigena.routing_kafka.store.entity.Suggestion;
import com.abarigena.routing_kafka.store.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionRepository extends CrudRepository<Suggestion, Long> {
    Suggestion findByCityStartAndCityEndAndPriceAndUser(String cityStart, String cityEnd, Double price, User user);
}
