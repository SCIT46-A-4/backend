package com.scit.iLog.repository;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResultEntity, Long> {
    @Query("select cr from ChildRecordEntity cr where cr.child.id = :childId and (cr.registerDate between :startDate and :endDate)")
    List<AnalysisResultEntity> findAllByCreatedAtBetween(
            @Param("childId") Long childId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
