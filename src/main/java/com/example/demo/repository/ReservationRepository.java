package com.example.demo.repository;

import com.example.demo.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.petsitter WHERE r.user.id = :userId ORDER BY r.requestedAt DESC")
    List<Reservation> findAllByUserIdOrderByRequestedAtDesc(@Param("userId") Long userId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.petsitter WHERE r.petsitter.id = :petsitterId ORDER BY r.requestedAt DESC")
    List<Reservation> findAllByPetsitterIdOrderByRequestedAtDesc(@Param("petsitterId") Long petsitterId);
} 