package com.scit.iLog.repository;

import com.scit.iLog.domain.child.ChildRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChildRecordRepository extends JpaRepository<ChildRecordEntity, Long> {
    @Query("select cr from ChildRecordEntity cr where cr.child.id = :childId and (cr.registerDate between :startDate and :endDate)")
    List<ChildRecordEntity> findAllByCreatedAtBetween(
            @Param("childId") Long childId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("select cr from ChildRecordEntity cr where cr.child.id = :childId and cr.id = :recordId")
    Optional<ChildRecordEntity> findByChildIdAndRecordId(@Param("childId") Long childId, @Param("recordId") Long recordId);

    Page<ChildRecordEntity> findByChildId(Long childId, Pageable pageable);
}
