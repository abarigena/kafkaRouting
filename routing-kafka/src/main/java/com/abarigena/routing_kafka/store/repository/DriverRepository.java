package com.abarigena.routing_kafka.store.repository;

import com.abarigena.routing_kafka.store.entity.Driver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends CrudRepository<Driver, Long> {
}
