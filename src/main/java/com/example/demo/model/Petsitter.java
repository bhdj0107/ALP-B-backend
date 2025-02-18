package com.example.demo.model;

import java.util.List;
import java.util.ArrayList;

import com.example.demo.dto.request.PetsitterRegisterRequest;
import com.example.demo.correlated.PetsitterCode;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Petsitter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private String introduction;
    private String experience;
    private String location;
    private String availableTime;
    private int price;

    @Builder.Default
    @OneToMany(mappedBy = "petsitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetsitterCode> petsitterCodes = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "petsitter_certifications", 
                    joinColumns = @JoinColumn(name = "petsitter_id"))
    @Column(name = "certification")
    private List<String> certifications = new ArrayList<>();

    @NotNull
    private Boolean isApproved;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "petsitter_services", 
                    joinColumns = @JoinColumn(name = "petsitter_id"))
    @Column(name = "service")
    private List<String> services = new ArrayList<>();

    public void updatePetsitterInfo(PetsitterRegisterRequest petsitterRegisterDto) {
        this.introduction = petsitterRegisterDto.getIntroduction();
        this.experience = petsitterRegisterDto.getExperience();
        this.location = petsitterRegisterDto.getLocation();
        this.availableTime = petsitterRegisterDto.getAvailableTime();
        this.price = petsitterRegisterDto.getPrice();
        this.services = petsitterRegisterDto.getServices();
        
        if (!this.certifications.equals(petsitterRegisterDto.getCertifications())) {
            this.certifications = petsitterRegisterDto.getCertifications();
            this.isApproved = false;
        }

    }
} 