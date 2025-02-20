package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisTargetEntity;

public interface AnalysisTargetRepository extends JpaRepository<AnalysisTargetEntity, Long> {

}
