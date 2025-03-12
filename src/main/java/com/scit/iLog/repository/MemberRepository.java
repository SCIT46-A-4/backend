package com.scit.iLog.repository;

import com.scit.iLog.domain.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findBySignInId(String signInId);
    boolean existsBySignInId(String signInId);
	Optional<MemberEntity> findByEmail(String email);
}
