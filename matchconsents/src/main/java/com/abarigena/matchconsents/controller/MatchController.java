package com.abarigena.matchconsents.controller;

import com.abarigena.matchconsents.service.MatchRequestService;
import com.abarigena.matchconsents.store.entity.MatchRequest;
import com.example.commondto.dto.ConsentResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    private final MatchRequestService matchRequestService;

    @Autowired
    public MatchController(final MatchRequestService matchRequestService) {
        this.matchRequestService = matchRequestService;
    }

    // Получение статуса запроса
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<MatchRequest> getRequestStatus(@PathVariable Long requestId) {
        MatchRequest request = matchRequestService.getRequestStatus(requestId);
        return ResponseEntity.ok(request);
    }

    // Обработка согласия от пользователя через REST API
    @PostMapping("/users/consent")
    public ResponseEntity<Void> processUserConsent(@RequestBody ConsentResponseDTO responseDTO) {
        if (responseDTO.getResponderType() != ConsentResponseDTO.ResponderType.USER) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        matchRequestService.processUserConsent(
                responseDTO.getRequestId(),
                responseDTO.getRespondentId(),
                responseDTO.getConsent()
        );
        return ResponseEntity.ok().build();
    }

    // Обработка согласия от водителя через REST API
    @PostMapping("/drivers/consent")
    public ResponseEntity<Void> processDriverConsent(@RequestBody ConsentResponseDTO responseDTO) {
        if (responseDTO.getResponderType() != ConsentResponseDTO.ResponderType.DRIVER) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        matchRequestService.processDriverConsent(
                responseDTO.getRequestId(),
                responseDTO.getRespondentId(),
                responseDTO.getConsent()
        );
        return ResponseEntity.ok().build();
    }
}
