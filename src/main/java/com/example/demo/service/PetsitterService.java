package com.example.demo.service;

import com.example.demo.dto.request.PetsitterRegisterRequest;
import com.example.demo.dto.response.CurrentPetsitterResponse;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.dto.response.PetsitterListResponse;
import com.example.demo.mapper.PetsitterMapper;
import com.example.demo.model.Petsitter;
import com.example.demo.repository.PetsitterRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.CodeGroupRepository;
import com.example.demo.repository.PetsitterCodeRepository;
import com.example.demo.repository.ReservationRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.model.CodeGroup;
import com.example.demo.correlated.PetsitterCode;
import com.example.demo.model.Reservation;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetsitterService {
    private final PetsitterRepository petsitterRepository;
    private final UserRepository userRepository;
    private final CodeGroupRepository codeGroupRepository;
    private final PetsitterCodeRepository petsitterCodeRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public CurrentPetsitterResponse registerPetsitter(PetsitterRegisterRequest request, HttpSession session) {
        // 기존 펫시터 정보 생성
        Petsitter petsitter = Petsitter.builder()
            .introduction(request.getIntroduction())
            .location(request.getLocation())
            .price(request.getPrice())
            .certifications(request.getCertifications())
            .availableTime(request.getAvailableTime())
            .isApproved(false)
            .user(userRepository.findById((Long) session.getAttribute("userID"))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")))
            .services(request.getServices())
            .experience(request.getExperience())
            .build();

        // 펫시터 저장
        Petsitter savedPetsitter = petsitterRepository.save(petsitter);

        // 선택된 코드 그룹들 연결
        if (request.getCodeGroupIds() != null && !request.getCodeGroupIds().isEmpty()) {
            for (String groupId : request.getCodeGroupIds()) {
                CodeGroup codeGroup = codeGroupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드 그룹입니다: " + groupId));
                
                PetsitterCode petsitterCode = PetsitterCode.builder()
                    .petsitter(savedPetsitter)
                    .codeGroup(codeGroup)
                    .build();
                
                petsitterCodeRepository.save(petsitterCode);
            }
        }

        return new PetsitterMapper().petsitterToCurrentPetsitterResponse(savedPetsitter);
    }

    public CurrentPetsitterResponse getCurrentPetsitter(HttpSession session) {
        Petsitter petsitter = petsitterRepository.findByUserId((Long) session.getAttribute("userID")).orElse(null);
        if (petsitter == null) {
            return null;
        }
        return new PetsitterMapper().petsitterToCurrentPetsitterResponse(petsitter);
    }

    @Transactional
    public CurrentPetsitterResponse updatePetsitterInfo(PetsitterRegisterRequest request, HttpSession session) {
        Petsitter petsitter = petsitterRepository.findByUserId((Long) session.getAttribute("userID"))
            .orElseThrow(() -> new RuntimeException("펫시터 정보를 찾을 수 없습니다."));

        // 기존 코드 그룹 연결 정보 삭제
        petsitterCodeRepository.deleteByPetsitterId(petsitter.getId());

        // 새로운 코드 그룹 연결 정보 생성
        if (request.getCodeGroupIds() != null && !request.getCodeGroupIds().isEmpty()) {
            for (String groupId : request.getCodeGroupIds()) {
                CodeGroup codeGroup = codeGroupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드 그룹입니다: " + groupId));
                
                PetsitterCode petsitterCode = PetsitterCode.builder()
                    .petsitter(petsitter)
                    .codeGroup(codeGroup)
                    .build();
                
                petsitterCodeRepository.save(petsitterCode);
            }
        }

        // 기존 정보 업데이트
        petsitter.updatePetsitterInfo(request);
        Petsitter savedPetsitter = petsitterRepository.save(petsitter);
        return new PetsitterMapper().petsitterToCurrentPetsitterResponse(savedPetsitter);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(HttpSession session) {
        Long userId = (Long) session.getAttribute("userID");
        Petsitter petsitter = petsitterRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("펫시터 정보를 찾을 수 없습니다."));
        
        List<Reservation> reservations = reservationRepository.findAllByPetsitterIdOrderByRequestedAtDesc(petsitter.getId());
        
        return reservations.stream()
            .map(r -> ReservationResponse.builder()
                .id(r.getId())
                .userName(r.getUser().getName())
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

    @Transactional(readOnly = true)
    public PetsitterListResponse getPetsitterList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Petsitter> petsitterPage = petsitterRepository.findAllByIsApprovedTrue(pageable);
        
        List<PetsitterListResponse.PetsitterSummary> petsitterSummaries = petsitterPage.getContent().stream()
            .map(petsitter -> {
                // 서비스 지역 정보 가져오기 (코드 그룹 중 지역 관련 코드만 필터링)
                List<String> serviceAreas = petsitter.getPetsitterCodes().stream()
                    .filter(pc -> pc.getCodeGroup().getGroup_id().startsWith("AREA"))  // 지역 코드 그룹만 필터링
                    .map(pc -> pc.getCodeGroup().getGroup_name())
                    .collect(Collectors.toList());

                return PetsitterListResponse.PetsitterSummary.builder()
                    .id(petsitter.getId())
                    .name(petsitter.getUser().getName())
                    .introduction(petsitter.getIntroduction())
                    .location(petsitter.getLocation())
                    .price(petsitter.getPrice())
                    .rating(0.0f)
                    .reviewCount(0)
                    .isApproved(petsitter.getIsApproved())
                    .availableTime(petsitter.getAvailableTime())
                    .services(petsitter.getServices())
                    .serviceAreas(serviceAreas)  // 필터링된 지역 정보 설정
                    .build();
            })
            .collect(Collectors.toList());
        
        return PetsitterListResponse.builder()
            .petsitters(petsitterSummaries)
            .totalPages(petsitterPage.getTotalPages())
            .totalElements(petsitterPage.getTotalElements())
            .currentPage(page)
            .hasNext(petsitterPage.hasNext())
            .hasPrevious(petsitterPage.hasPrevious())
            .build();
    }
}