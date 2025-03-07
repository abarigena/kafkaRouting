package com.abarigena.notificationkafka.store.repository;

import com.abarigena.notificationkafka.store.entity.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findByRecipientIdAndRecipientType(Long recipientId, String recipientType);
    List<Notification> findByRequestId(Long requestId);
    List<Notification> findByRecipientIdAndRecipientTypeAndIsRead(Long recipientId, String recipientType, Boolean isRead);
}
