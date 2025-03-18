package com.scit.iLog.repository;

import com.scit.iLog.domain.mentalsurvey.MentalSurveyResponseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MentalSurveyResponseRepository extends MongoRepository<MentalSurveyResponseEntity, String> {
    @Query("{ 'childId': ?0, 'respondentId': ?1, 'createdAt': { $gte: ?2, $lte: ?3 } }")
    List<MentalSurveyResponseEntity> findByChildIdAndRespondentIdAndCreatedAtBetween(
            @Param("childId") Long childId,
            @Param("respondentId") Long respondentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("{ 'childId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<MentalSurveyResponseEntity> findByChildCreatedAtBetween(
            @Param("childId") Long childId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<MentalSurveyResponseEntity> findAllByCreatedAtBetweenOrderByCreatedAtAsc(LocalDateTime startDate, LocalDateTime endDate);

    List<MentalSurveyResponseEntity> findAllByChildIdAndCreatedAtBetween(Long childId, LocalDateTime startDate, LocalDateTime endDate, Sort sort);

    List<MentalSurveyResponseEntity> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Sort sort);
}
