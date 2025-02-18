package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime; 
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.example.demo.model.enums.PaymentStatus;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private int amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    private String type;
    private String name;
    private String email;

    private String approvalNumber;
    private LocalDateTime approvedAt;

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public void updatePaymentInfo(String approvalNumber, LocalDateTime approvedAt) {
        this.approvalNumber = approvalNumber;
        this.approvedAt = approvedAt;
    }
}
