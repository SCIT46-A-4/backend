package com.scit.iLog.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.domain.child.ChildEntity;

public interface ChildDiaryRepository extends JpaRepository<ChildDiaryEntity, Long>
	{

		// 25/2/6 날짜 기준으로 정렬하고 일기장을 출력하는 페이징을 적용하는 메서드
//		Page<ChildEntity> findByChildOrderByCreatedAtDesc(Optional<ChildEntity> child, Pageable page);
		Page<ChildDiaryEntity> findByChildOrderByCreatedAtDesc(ChildEntity childEntity, Pageable page);

		Optional<ChildDiaryEntity> findByChildId(Long childId);

		ChildDiaryEntity save(String content);

		Optional<ChildDiaryEntity> findByAuthor(MemberEntity author);

	}
