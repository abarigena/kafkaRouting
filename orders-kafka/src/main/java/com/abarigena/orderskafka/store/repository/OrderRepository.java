package com.abarigena.orderskafka.store.repository;

import com.abarigena.orderskafka.store.entity.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    List<Order> findAllByDriverId(Long driverId);

    List<Order> findAllByUserId(Long userId);
}
