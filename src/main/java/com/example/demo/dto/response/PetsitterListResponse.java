package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PetsitterListResponse {
    private List<PetsitterSummary> petsitters;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private boolean hasNext;
    private boolean hasPrevious;

    @Getter
    @Builder
    public static class PetsitterSummary {
        private Long id;
        private String name;
        private String introduction;
        private String location;
        private int price;
        private float rating;
        private int reviewCount;
        private boolean isApproved;
        private String availableTime;
        private List<String> services;
        private List<String> serviceAreas;
    }
} 