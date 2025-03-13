package com.scit.iLog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.claim.ClaimEntity;
import com.scit.iLog.domain.member.MemberEntity;

import java.util.List;

public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {
    Page<ClaimEntity> findByAuthor(MemberEntity author, Pageable pageable);
    List<ClaimEntity> findAllByAuthor(MemberEntity author);
}
