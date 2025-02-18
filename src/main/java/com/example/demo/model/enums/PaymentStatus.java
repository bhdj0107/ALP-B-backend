package com.example.demo.model.enums;

public enum PaymentStatus {
    READY("결제준비"),
    IN_PROGRESS("결제중"),
    SUCCESS("결제완료"),
    FAILED("결제실패"),
    CANCELLED("결제취소");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 