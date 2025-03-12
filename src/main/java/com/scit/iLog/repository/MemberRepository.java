package com.scit.iLog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scit.iLog.domain.member.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findBySignInId(String signInId);
    boolean existsBySignInId(String signInId);
    
    @Query("SELECT m.id FROM MemberEntity m WHERE m.email = :email")
	Long findByEmail(@Param("email")String inviteeEmail);
}
