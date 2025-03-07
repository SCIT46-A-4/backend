package com.scit.iLog.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultEntity;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResultEntity, Long> {
//    @Query("select cr from ChildRecordEntity cr where cr.child.id = :childId and (cr.registerDate between :startDate and :endDate)")
	@Query("select a from AnalysisResultEntity a where a.analysisTarget.child.id = :childId and (a.analysisTarget.registerDate between :startDate and :endDate)")
	List<AnalysisResultEntity> findAllByCreatedAtBetween(
            @Param("childId") Long childId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
