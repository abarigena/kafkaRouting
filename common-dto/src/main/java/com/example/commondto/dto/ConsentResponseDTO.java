package com.example.commondto.dto;

import java.io.Serializable;

// DTO для ответов на уведомления
public class ConsentResponseDTO implements Serializable {

    private Long requestId;
    private Long respondentId;  // ID того, кто отвечает (водитель или пассажир)
    private Boolean consent;    // Согласие или отказ
    private ResponderType responderType;  // Тип отвечающего

    // Enum для типа отвечающего
    public enum ResponderType {
        USER,
        DRIVER
    }

    // Конструкторы, геттеры, сеттеры
    public ConsentResponseDTO() {}

    public ConsentResponseDTO(Long requestId, Long respondentId, Boolean consent,
                              ResponderType responderType) {
        this.requestId = requestId;
        this.respondentId = respondentId;
        this.consent = consent;
        this.responderType = responderType;
    }

    // Геттеры и сеттеры
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getRespondentId() {
        return respondentId;
    }

    public void setRespondentId(Long respondentId) {
        this.respondentId = respondentId;
    }

    public Boolean getConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public ResponderType getResponderType() {
        return responderType;
    }

    public void setResponderType(ResponderType responderType) {
        this.responderType = responderType;
    }

    @Override
    public String toString() {
        return "ConsentResponseDTO{" +
                "requestId=" + requestId +
                ", respondentId=" + respondentId +
                ", consent=" + consent +
                ", responderType=" + responderType +
                '}';
    }
}
