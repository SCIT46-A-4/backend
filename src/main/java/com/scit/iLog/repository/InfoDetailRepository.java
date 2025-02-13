package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.domain.child.ChildRecordEntity;

import java.util.Optional;

public interface InfoDetailRepository extends JpaRepository<ChildRecordEntity, Long> 
	{    
}
