package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.child.AnalysisResultEntity;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResultEntity, Long> {

}
