package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.request.PaymentEnrollRequest;
import com.example.demo.dto.request.PaymentStatusUpdateRequest;
import com.example.demo.model.Payment;
import com.example.demo.model.Reservation;
import com.example.demo.model.enums.ReservationStatus;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.ReservationRepository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import com.example.demo.model.enums.PaymentStatus;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Payment initiatePayment(PaymentEnrollRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        Payment payment = Payment.builder()
            .id(request.getMerchantUid())
            .reservation(reservation)
            .amount(request.getAmount())
            .status(PaymentStatus.IN_PROGRESS)
            .type(request.getType())
            .name(request.getName())
            .email(request.getEmail())
            .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public void completePayment(PaymentStatusUpdateRequest request) {
        Payment payment = paymentRepository.findById(request.getMerchantUid())
            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        PaymentStatus newStatus = PaymentStatus.valueOf(request.getStatus().toUpperCase());
        payment.updateStatus(newStatus);
        
        if (newStatus == PaymentStatus.SUCCESS) {
            payment.updatePaymentInfo(request.getApprovalNumber(), request.getApprovedAt());
            Reservation reservation = payment.getReservation();
            reservation.setStatus(ReservationStatus.CONFIRMED);
        }
    }

    @Transactional(readOnly = true)
    public Payment getPayment(String id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
    }
}
    