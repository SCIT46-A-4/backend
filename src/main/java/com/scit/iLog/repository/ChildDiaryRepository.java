package com.scit.iLog.repository;

import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.domain.child.ChildEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildDiaryRepository extends JpaRepository<ChildDiaryEntity, Long>
	{
		// 25/2/6 날짜 기준으로 정렬하고 일기장을 출력하는 페이징을 적용하는 메서드
		Page<ChildDiaryEntity> findByChildOrderByCreatedAtDesc(ChildEntity child, Pageable page);
	}
