package com.scit.iLog.repository;

import com.scit.iLog.domain.mentalsurvey.MentalSurveyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MentalSurveyRepository extends MongoRepository<MentalSurveyEntity, String> {
}
