package com.example.demo.controller;

import com.example.demo.dto.request.PetsitterRegisterRequest;
import com.example.demo.dto.response.CurrentPetsitterResponse;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.dto.response.PetsitterListResponse;

import com.example.demo.service.PetsitterService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/petsitters")
public class PetsitterController {

    @Autowired
    private PetsitterService petsitterService;

    // 펫시터 정보 등록 신청을 받는 엔드포인트
    @PostMapping("/register")
    public ResponseEntity<CurrentPetsitterResponse> registerPetsitter(@RequestBody PetsitterRegisterRequest petsitterRegisterDto, HttpSession session) {
        CurrentPetsitterResponse savedPetsitter = petsitterService.registerPetsitter(petsitterRegisterDto, session);
        return ResponseEntity.ok(savedPetsitter);
    }

    // 현재 세션에 있는 펫시터 정보를 반환하는 엔드포인트
    @GetMapping("/info")
    public ResponseEntity<CurrentPetsitterResponse> getCurrentPetsitter(HttpSession session) {
        CurrentPetsitterResponse currentPetsitterResponse = petsitterService.getCurrentPetsitter(session);
        if (currentPetsitterResponse == null) {
            return ResponseEntity.status(401).body(null);
        } else {
            return ResponseEntity.ok(currentPetsitterResponse);
        }
    }

    // 펫시터 정보 수정을 하는 엔드포인트
    @PostMapping("/info")
    public ResponseEntity<CurrentPetsitterResponse> updatePetsitterInfo(@RequestBody PetsitterRegisterRequest petsitterRegisterDto, HttpSession session) {
        CurrentPetsitterResponse updatedPetsitter = petsitterService.updatePetsitterInfo(petsitterRegisterDto, session);
        return ResponseEntity.ok(updatedPetsitter);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(HttpSession session) {
        List<ReservationResponse> reservations = petsitterService.getMyReservations(session);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping
    public ResponseEntity<PetsitterListResponse> getPetsitterList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PetsitterListResponse response = petsitterService.getPetsitterList(page, size);
        return ResponseEntity.ok(response);
    }
}