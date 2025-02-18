package com.example.demo.repository;

import com.example.demo.model.Petsitter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PetsitterRepository extends JpaRepository<Petsitter, Long> {
    @Query("SELECT p FROM Petsitter p WHERE p.user.id = :userId")
    public Optional<Petsitter> findByUserId(@Param("userId") Long userId);

    Page<Petsitter> findAllByIsApprovedTrue(Pageable pageable);
} 