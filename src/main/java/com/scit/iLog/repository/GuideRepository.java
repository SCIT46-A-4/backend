package com.scit.iLog.repository;

import com.scit.iLog.domain.GuideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GuideRepository extends JpaRepository<GuideEntity, Long> {

    List<GuideEntity> findByContentContains(String content);

    List<GuideEntity> findByTitleContains(String title);

    List<GuideEntity> findByContentContainingIgnoreCase(String content);

    List<GuideEntity> findByTitleContainingIgnoreCase(String title);

}
