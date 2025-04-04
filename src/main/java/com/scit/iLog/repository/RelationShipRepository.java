package com.scit.iLog.repository;

import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelationShipRepository extends JpaRepository<RelationShipEntity, Long> {
    void deleteByMember(MemberEntity member);

    @Query("select r from RelationShipEntity r where r.member.id = :memberId")
    List<RelationShipEntity> findAllByMemberId(@Param("memberId") Long memberId);

    void deleteAllByMember(MemberEntity member);

    Optional<RelationShipEntity> findByMemberAndChild(MemberEntity member, ChildEntity child);

    boolean existsByChildAndMember(ChildEntity child, MemberEntity invitee);
}
