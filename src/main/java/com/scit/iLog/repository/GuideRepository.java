package com.scit.iLog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scit.iLog.domain.GuideEntity;


@Repository
public interface GuideRepository extends JpaRepository<GuideEntity, Long> {

	List<GuideEntity> findByContentContains(String content);

	List<GuideEntity> findByTitleContains(String title);
	
    List<GuideEntity> findByContentContainingIgnoreCase(String content);

    List<GuideEntity> findByTitleContainingIgnoreCase(String title);

}
