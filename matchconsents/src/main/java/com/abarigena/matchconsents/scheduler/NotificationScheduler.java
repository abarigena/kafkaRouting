package com.abarigena.matchconsents.scheduler;


import com.abarigena.matchconsents.store.entity.MatchRequest;
import com.abarigena.matchconsents.store.repository.MatchRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    private final MatchRequestRepository requestRepository;

    @Autowired
    public NotificationScheduler(MatchRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    // Запускаем проверку каждые 15 минут
    @Scheduled(fixedRate = 900000)
    @Transactional
    public void processExpiredRequests() {
        logger.info("Starting expired requests processing");

        // Находим запросы, созданные более 24 часов назад и всё ещё в статусе PENDING
        LocalDateTime expirationTime = LocalDateTime.now().minusHours(24);
        List<MatchRequest> expiredRequests =
                requestRepository.findByStatusAndCreatedAtBefore("PENDING", expirationTime);

        expiredRequests.forEach(request -> {
            logger.info("Processing expired request: {}", request);

            // Если нет ответа от пользователя, автоматически устанавливаем отказ
            if (request.getUserConsent() == null) {
                request.setUserConsent(false);
            }

            // Если нет ответа от водителя, автоматически устанавливаем отказ
            if (request.getDriverConsent() == null) {
                request.setDriverConsent(false);
            }

            // Устанавливаем статус как EXPIRED
            request.setStatus("EXPIRED");
            requestRepository.save(request);
        });

        logger.info("Processed {} expired requests", expiredRequests.size());
    }
}