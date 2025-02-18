package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import com.example.demo.model.CodeDetail;
import java.util.List;

@Repository
public interface CodeDetailRepository extends JpaRepository<CodeDetail, String> {
    // 메서드 이름으로 쿼리 생성 방식 사용
    // List<CodeDetail> findByCodeGroup_GroupId(String groupId);
    
    // 또는 명시적 JPQL 쿼리 사용
    @Query("SELECT cd FROM CodeDetail cd WHERE cd.codeGroup.group_id = :groupId")
    List<CodeDetail> findByCodeGroup_GroupId(@Param("groupId") String groupId);

    @Modifying
    @Query("DELETE FROM CodeDetail cd WHERE cd.codeGroup.group_id = :groupId")
    void deleteByCodeGroup_GroupIdIn(@Param("groupId") String groupId);
}
