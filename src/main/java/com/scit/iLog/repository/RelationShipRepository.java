package com.scit.iLog.repository;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.domain.RelationShipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationShipRepository extends JpaRepository<RelationShipEntity, Long> {
    void deleteByMember(MemberEntity member);
}
