package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.request.PaymentEnrollRequest;
import com.example.demo.dto.request.PaymentStatusUpdateRequest;
import com.example.demo.dto.response.PaymentResponse;
import com.example.demo.model.Payment;
import com.example.demo.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public ResponseEntity<PaymentResponse> preparePayment(@RequestBody PaymentEnrollRequest request) {
        Payment payment = paymentService.initiatePayment(request);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    @PostMapping("/complete")
    public ResponseEntity<Void> completePayment(@RequestBody PaymentStatusUpdateRequest request) {
        paymentService.completePayment(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String id) {
        Payment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }
}
