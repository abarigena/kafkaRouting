package com.abarigena.userkafka.store.repository;

import com.abarigena.userkafka.store.entity.Driver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends CrudRepository<Driver, Long> {

}
