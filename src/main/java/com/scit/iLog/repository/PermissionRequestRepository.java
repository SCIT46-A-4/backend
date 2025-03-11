package com.scit.iLog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scit.iLog.domain.permition.PermissionRequestEntity;

@Repository
public interface PermissionRequestRepository extends JpaRepository<PermissionRequestEntity, Long>
	{
	  Optional<PermissionRequestEntity> findByRequestLinkCode(String requestLinkCode);

	Optional<PermissionRequestEntity> findByRequesterIdAndChildId(Long memberId, Long childId);

	}
