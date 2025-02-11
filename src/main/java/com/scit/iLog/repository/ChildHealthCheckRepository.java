package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scit.iLog.domain.HealthSurveyEntity;

@Repository
public interface ChildHealthCheckRepository extends JpaRepository<HealthSurveyEntity, Long>
	{

	}
