package com.abarigena.routing_kafka.store.repository;

import com.abarigena.routing_kafka.store.entity.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends CrudRepository<Route, Long> {
    Route findByCityStartAndCityEndAndPriceAndDriverId(String cityStart, String cityEnd, Double price, Long driverId);
}
