package com.scit.iLog.repository;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisTypeRepository extends JpaRepository<AnalysisTypeEntity, Long> {
}
