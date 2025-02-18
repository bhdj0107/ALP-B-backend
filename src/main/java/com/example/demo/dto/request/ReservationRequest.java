package com.example.demo.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationRequest {
    private Long petsitterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private int price;
    private List<String> services;
} 