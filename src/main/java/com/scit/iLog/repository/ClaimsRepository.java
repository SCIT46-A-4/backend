package com.scit.iLog.repository;

import com.scit.iLog.domain.claim.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimsRepository extends JpaRepository<ClaimEntity, Long> {

}
