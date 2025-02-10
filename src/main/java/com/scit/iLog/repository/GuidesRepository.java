package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scit.iLog.domain.GuideEntity;

@Repository
public interface GuidesRepository extends JpaRepository<GuideEntity, Long> {

}
