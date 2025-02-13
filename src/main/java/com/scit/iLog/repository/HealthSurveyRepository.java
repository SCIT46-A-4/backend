package com.scit.iLog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scit.iLog.domain.HealthSurveyEntity;

@Repository
public interface HealthSurveyRepository extends JpaRepository<HealthSurveyEntity, Long> {
	
	// 페이징 기능 추가 (offset, limit 적용)
    Page<HealthSurveyEntity> findAll(Pageable pageable);
}