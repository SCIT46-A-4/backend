package com.scit.iLog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.claim.ClaimEntity;
import com.scit.iLog.domain.member.MemberEntity;

public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {
	
//	List<ClaimEntity> findByAuthor(MemberEntity author); // ✅ 특정 사용자의 문의 사항 조회
	// 페이징 지원하도록 변경!
    Page<ClaimEntity> findByAuthor(MemberEntity author, Pageable pageable);
}
