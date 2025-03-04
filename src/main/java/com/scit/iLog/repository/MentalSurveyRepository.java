package com.scit.iLog.repository;

import com.scit.iLog.domain.mentalsurvey.MentalSurveyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MentalSurveyRepository extends MongoRepository<MentalSurveyEntity, String> {
}
