package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.model.Reservation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponse {
    private Long id;
    private String userName;
    private String status;
    private String description;
    private int price;
    private LocalDateTime requestedAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> selectedServices;

    public static ReservationResponse from(Reservation reservation) {
        return ReservationResponse.builder()
            .id(reservation.getId())
            .userName(reservation.getUser().getName())
            .status(reservation.getStatus().getDescription())
            .description(reservation.getDescription())
            .price(reservation.getPrice())
            .requestedAt(reservation.getRequestedAt())
            .startTime(reservation.getReservationStartAt())
            .endTime(reservation.getReservationEndAt())
            .selectedServices(reservation.getSelectedServices())
            .build();
    }
} 