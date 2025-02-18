package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeGroup {
    @Id
    private String group_id;
    private String group_name;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
