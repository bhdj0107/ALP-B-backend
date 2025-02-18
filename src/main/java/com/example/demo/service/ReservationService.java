package com.example.demo.service;

import com.example.demo.dto.request.ReservationRequest;
import com.example.demo.model.Reservation;
import com.example.demo.model.User;
import com.example.demo.model.enums.ReservationStatus;
import com.example.demo.model.Petsitter;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.PetsitterRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.response.ReservationResponse;

import java.time.LocalDateTime;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PetsitterRepository petsitterRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userID");
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Petsitter petsitter = petsitterRepository.findById(request.getPetsitterId())
            .orElseThrow(() -> new RuntimeException("펫시터를 찾을 수 없습니다."));

        if (!petsitter.getIsApproved()) {
            throw new RuntimeException("승인되지 않은 펫시터입니다.");
        }

        // 선택된 서비스가 펫시터가 제공하는 서비스에 포함되는지 확인
        if (!petsitter.getServices().containsAll(request.getServices())) {
            throw new IllegalArgumentException("선택한 서비스 중 펫시터가 제공하지 않는 서비스가 있습니다.");
        }

        Reservation reservation = Reservation.builder()
            .user(user)
            .petsitter(petsitter)
            .status(ReservationStatus.CONFIRMING)
            .description(request.getDescription())
            .price(request.getPrice())
            .requestedAt(LocalDateTime.now())
            .reservationStartAt(request.getStartTime())
            .reservationEndAt(request.getEndTime())
            .selectedServices(request.getServices())
            .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(HttpSession session) {
        Long userId = (Long) session.getAttribute("userID");
        List<Reservation> reservations = reservationRepository.findAllByUserIdOrderByRequestedAtDesc(userId);
        
        return reservations.stream()
            .map(r -> ReservationResponse.builder()
                .id(r.getId())
                .userName(r.getPetsitter().getUser().getName())
                .status(r.getStatus().getDescription())
                .description(r.getDescription())
                .price(r.getPrice())
                .requestedAt(r.getRequestedAt())
                .startTime(r.getReservationStartAt())
                .endTime(r.getReservationEndAt())
                .selectedServices(r.getSelectedServices())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse approveReservation(Long reservationId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userID");
            if (userId == null) {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            // 예약 정보 조회
            Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

            // 펫시터 정보 조회
            Petsitter petsitter = petsitterRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("펫시터 정보를 찾을 수 없습니다."));

            // 해당 펫시터의 예약인지 확인
            if (!reservation.getPetsitter().getId().equals(petsitter.getId())) {
                throw new RuntimeException("해당 예약에 대한 권한이 없습니다.");
            }

            // 상태가 CONFIRMING인지 확인
            if (reservation.getStatus() != ReservationStatus.CONFIRMING) {
                throw new RuntimeException("승인 대기 상태의 예약만 승인할 수 있습니다.");
            }

            // 상태를 PERCHASING으로 변경
            reservation.setStatus(ReservationStatus.PERCHASING);
            
            // 저장 및 응답
            Reservation updatedReservation = reservationRepository.save(reservation);
            return ReservationResponse.from(updatedReservation);
            
        } catch (Exception e) {
            // 로그 추가
            log.error("예약 승인 중 오류 발생: ", e);
            throw new RuntimeException("예약 승인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 