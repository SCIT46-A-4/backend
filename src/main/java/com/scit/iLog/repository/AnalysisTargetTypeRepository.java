package com.scit.iLog.repository;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisTargetTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisTargetTypeRepository extends JpaRepository<AnalysisTargetTypeEntity, Long> {
}
