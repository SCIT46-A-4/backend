package com.scit.iLog.repository;

import com.scit.iLog.domain.GuideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GuideRepository extends JpaRepository<GuideEntity, Long> {

}
