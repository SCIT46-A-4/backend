package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

}
