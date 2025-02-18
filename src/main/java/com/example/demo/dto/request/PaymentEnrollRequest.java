package com.example.demo.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentEnrollRequest {
    private String merchantUid;
    private Long reservationId;
    private int amount;
    private String type;
    private String name;
    private String email;
}
