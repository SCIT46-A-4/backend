package com.scit.iLog.repository;

import com.scit.iLog.domain.child.ChildBackGroundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildBackGroundRepository extends JpaRepository<ChildBackGroundEntity, Long> {

}
