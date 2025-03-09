package com.scit.iLog.repository;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResultEntity, Long> {
    @Query("select ar from AnalysisResultEntity ar where ar.analysisTarget.child.id = :childId and (ar.createdAt between :startDate and :endDate)")
    List<AnalysisResultEntity> findAllByCreatedAtBetween(
            @Param("childId") Long childId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("select a from AnalysisResultEntity a where a.analysisTarget.child.id = :childId and (a.analysisTarget.registerDate between :startDate and :endDate)")
    List<AnalysisResultEntity> findAllByRegisterDateBetween(
            @Param("childId") Long childId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<AnalysisResultEntity> findAllByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
