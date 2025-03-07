package com.abarigena.matchconsents.store.repository;

import com.abarigena.matchconsents.store.entity.MatchRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRequestRepository extends CrudRepository<MatchRequest, Long> {
    List<MatchRequest> findByStatus(String status);

    List<MatchRequest> findByUserIdAndStatus(Long userid, String status);

    List<MatchRequest> findByDriverIdAndStatus(Long driverId, String status);

    // Находим просроченные запросы для автоматического обновления
    List<MatchRequest> findByStatusAndCreatedAtBefore(String status, LocalDateTime time);
}
