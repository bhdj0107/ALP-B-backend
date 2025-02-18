package com.example.demo.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import com.example.demo.model.Payment;
import com.example.demo.model.enums.PaymentStatus;

@Getter
@Builder
public class PaymentResponse {
    private String id;
    private Long reservationId;
    private int amount;
    private PaymentStatus status;
    private String type;
    private String name;
    private String email;
    private String approvalNumber;
    private LocalDateTime approvedAt;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
            .id(payment.getId())
            .reservationId(payment.getReservation().getId())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .type(payment.getType())
            .name(payment.getName())
            .email(payment.getEmail())
            .approvalNumber(payment.getApprovalNumber())
            .approvedAt(payment.getApprovedAt())
            .build();
    }
} 