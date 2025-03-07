package com.abarigena.notificationkafka.Controller;

import com.abarigena.notificationkafka.service.NotificationService;
import com.abarigena.notificationkafka.store.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Получение активных уведомлений для пользователя
    @GetMapping("/users/{userId}/active")
    public ResponseEntity<List<Notification>> getActiveUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getActiveNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // Получение активных уведомлений для водителя
    @GetMapping("/drivers/{driverId}/active")
    public ResponseEntity<List<Notification>> getActiveDriverNotifications(@PathVariable Long driverId) {
        List<Notification> notifications = notificationService.getActiveNotificationsForDriver(driverId);
        return ResponseEntity.ok(notifications);
    }

    // Получение истории уведомлений водителя
    @GetMapping("/drivers/{driverId}/history")
    public ResponseEntity<List<Notification>> getDriverNotificationHistory(@PathVariable Long driverId) {
        List<Notification> notifications = notificationService.getDriverNotificationHistory(driverId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/users/{userId}/history")
    public ResponseEntity<List<Notification>> getUserNotificationHistory(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUserNotificationHistory(userId);
        return ResponseEntity.ok(notifications);
    }

    // Пометить уведомление как прочитанное
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
