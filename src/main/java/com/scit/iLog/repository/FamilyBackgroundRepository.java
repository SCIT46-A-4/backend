package com.scit.iLog.repository;

import com.scit.iLog.domain.child.FamilyBackGround;
import com.scit.iLog.domain.child.FamilyBackGroundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // 2025-02-28 / 김은진 / 가정환경 데이터 저장
public interface FamilyBackgroundRepository extends JpaRepository<FamilyBackGroundEntity, Long> {
    Optional<FamilyBackGroundEntity> findByFamilyBackGround(FamilyBackGround familyBackGround);
}
