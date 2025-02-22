package com.scit.iLog.repository;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisSatisfactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisSatisfactionRepository extends JpaRepository<AnalysisSatisfactionEntity, Long> {
}
