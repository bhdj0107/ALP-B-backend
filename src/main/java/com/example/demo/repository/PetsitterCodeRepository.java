package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.correlated.PetsitterCode;

@Repository
public interface PetsitterCodeRepository extends JpaRepository<PetsitterCode, Long> {
    @Modifying
    @Query("DELETE FROM PetsitterCode pc WHERE pc.petsitter.id = :petsitterId")
    void deleteByPetsitterId(@Param("petsitterId") Long petsitterId);
} 