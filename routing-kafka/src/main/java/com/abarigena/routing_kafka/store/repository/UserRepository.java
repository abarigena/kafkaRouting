package com.abarigena.routing_kafka.store.repository;

import com.abarigena.routing_kafka.store.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
