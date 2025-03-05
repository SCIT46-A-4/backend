package com.scit.iLog.repository;

import com.scit.iLog.domain.mentalsurvey.MentalSurveyResponseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MentalSurveyResponseRepository extends MongoRepository<MentalSurveyResponseEntity, String> {
    @Query("{ 'childId': ?0, 'respondentId': ?1, 'createdAt': { $gte: ?2, $lte: ?3 } }")
    List<MentalSurveyResponseEntity> findByChildIdAndRespondentIdAndCreatedAtBetween(
            @Param("childId") Long childId,
            @Param("respondentId") Long respondentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<MentalSurveyResponseEntity> findAllByCreatedAtBetweenOrderByCreatedAtAsc(LocalDateTime startDate, LocalDateTime endDate);
}
