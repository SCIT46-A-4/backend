package com.scit.iLog.repository;

import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.mentalsurvey.MentalSurveyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MentalSurveyRepository extends MongoRepository<MentalSurveyEntity, String> {

    List<MentalSurveyEntity> findByTitleContainingAndType(String title, RelationType type);
}
