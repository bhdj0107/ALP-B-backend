package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.JoinColumn;  
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import com.example.demo.model.enums.ReservationStatus;
import lombok.Setter;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "petsitter_id")
    @NotNull
    private Petsitter petsitter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String description;
    private int price;

    private LocalDateTime requestedAt;
    private LocalDateTime reservationStartAt;
    private LocalDateTime reservationEndAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "reservation_services",
        joinColumns = @JoinColumn(name = "reservation_id")
    )
    @Column(name = "service")
    @Builder.Default
    private List<String> selectedServices = new ArrayList<>();

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setSelectedServices(List<String> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public List<String> getSelectedServices() {
        return selectedServices;
    }
}
