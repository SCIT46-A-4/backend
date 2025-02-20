package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.claim.ClaimAnswerEntity;

public interface ClaimAnswerRepository extends JpaRepository<ClaimAnswerEntity, Long> {

}
