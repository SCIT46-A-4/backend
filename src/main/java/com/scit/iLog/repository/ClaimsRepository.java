package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.ClaimsEntity;

public interface ClaimsRepository extends JpaRepository<ClaimsEntity, Long> {

}
