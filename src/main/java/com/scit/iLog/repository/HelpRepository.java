package com.scit.iLog.repository;

import com.scit.iLog.domain.HelpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HelpRepository extends JpaRepository<HelpEntity, Long> {

}
