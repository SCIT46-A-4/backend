package com.scit.iLog.repository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.ChildDiaryEntity;
import com.scit.iLog.domain.ChildEntity;

public interface ChildRepository extends JpaRepository<ChildEntity, Long>
	{
		
	}
