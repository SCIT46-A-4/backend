package com.scit.iLog.repository;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisTargetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisTargetRepository extends JpaRepository<AnalysisTargetEntity, Long> {
    Page<AnalysisTargetEntity> findAllByChild_Id(Long childId, Pageable pageable);
}
