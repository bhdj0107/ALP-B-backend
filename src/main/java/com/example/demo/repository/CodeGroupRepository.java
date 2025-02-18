package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.CodeGroup;

@Repository
public interface CodeGroupRepository extends JpaRepository<CodeGroup, String> {
}
