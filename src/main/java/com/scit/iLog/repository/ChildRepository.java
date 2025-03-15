package com.scit.iLog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.child.ChildEntity;

/* 2025-02-06 이도훈 아이 정보 레파지토리 생성 */
public interface ChildRepository extends JpaRepository<ChildEntity, Long> {
    // 2025-03-04 / 김은진 / 교사용 대시보드에서 아동 리스트 정렬
    List<ChildEntity> findAllByOrderByNameAsc();

    List<ChildEntity> findAllByOrderByBirthDateAsc();

    List<ChildEntity> findAllByOrderByCreatedAtDesc();

}