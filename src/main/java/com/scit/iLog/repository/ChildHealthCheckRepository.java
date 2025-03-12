package com.scit.iLog.repository;

import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import com.scit.iLog.domain.member.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildHealthCheckRepository extends JpaRepository<HealthCheckEntity, Long> {
		// 페이징 기능 추가 (offset, limit 적용)
		Page<HealthCheckEntity> findAll(Pageable pageable);

		Optional<HealthCheckEntity> findByChildRecordId(Long childRecordId);

		List<HealthCheckEntity> findAllByMember(MemberEntity member);
}
