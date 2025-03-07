package com.abarigena.userkafka.store.repository;

import com.abarigena.userkafka.store.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
