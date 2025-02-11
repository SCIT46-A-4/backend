package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.MemberEntity;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByMemberId(String memberId);
}
