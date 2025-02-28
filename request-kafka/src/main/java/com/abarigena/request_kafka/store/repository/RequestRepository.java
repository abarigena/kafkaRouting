package com.abarigena.request_kafka.store.repository;

import com.abarigena.request_kafka.store.entity.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {

    boolean existsByRouteIdAndSuggestionIdAndUserConsentIsTrueAndDriverConsentIsTrue(Long routeId, Long suggestionId);
}
