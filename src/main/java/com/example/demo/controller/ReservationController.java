package com.example.demo.controller;

import com.example.demo.dto.request.ReservationRequest;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.service.ReservationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request,
            HttpSession session) {
        ReservationResponse response = reservationService.createReservation(request, session);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(HttpSession session) {
        List<ReservationResponse> reservations = reservationService.getMyReservations(session);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/{reservationId}/approve")
    public ResponseEntity<?> approveReservation(
            @PathVariable("reservationId") Long reservationId,
            HttpSession session) {
        try {
            ReservationResponse response = reservationService.approveReservation(reservationId, session);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("예약 승인 처리 중 오류: ", e);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("예약 승인 처리 중 예상치 못한 오류: ", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버 내부 오류가 발생했습니다."));
        }
    }

    @Getter
    @AllArgsConstructor
    private static class ErrorResponse {
        private String message;
    }
} 