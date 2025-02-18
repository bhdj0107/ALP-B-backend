package com.example.demo.model.enums;

public enum ReservationStatus {
    CONFIRMING("승인중"),
    PERCHASING("결제중"),
    CONFIRMED("확정됨"),
    CANCELLED("취소됨");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 