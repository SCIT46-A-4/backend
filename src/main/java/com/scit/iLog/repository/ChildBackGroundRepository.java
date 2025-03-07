package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scit.iLog.domain.child.ChildBackGroundEntity;

@Repository 
public interface ChildBackGroundRepository extends JpaRepository<ChildBackGroundEntity, Long> {

}
