package com.scit.iLog.repository;

import com.scit.iLog.domain.mentalsurvey.MentalSurveyResponseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MentalSurveyResponseRepository extends MongoRepository<MentalSurveyResponseEntity, String> {
    @Query("{ 'childId': #{#childId}, 'respondentId': #{#repondentId}, 'createdAt': { $gte: #{#startDate}, $lte: #{#endDate} } }")
    List<MentalSurveyResponseEntity> findByChildIdAndRespondentIdAndCreatedAtBetween(
            @Param("childId") Long childId,
            @Param("respondentId") Long respondentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
