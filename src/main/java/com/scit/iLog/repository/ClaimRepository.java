package com.scit.iLog.repository;

import com.scit.iLog.domain.claim.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {

}
