package com.scit.iLog.repository;

import com.scit.iLog.domain.child.ChildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/* 2025-02-06 이도훈 아이 정보 레파지토리 생성 */
public interface ChildRepository extends JpaRepository<ChildEntity, Long> {
}
