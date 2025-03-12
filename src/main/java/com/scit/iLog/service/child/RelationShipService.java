package com.scit.iLog.service.child;

import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.MemberRepository;
import com.scit.iLog.repository.RelationShipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationShipService {
    private final RelationShipRepository relationShipRepository;
    private final ChildRepository childRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void deleteAllChildrenOf(Long memberId) {
        List<RelationShipEntity> relationShips = relationShipRepository.findAllByMemberId(memberId);
        List<ChildEntity> children = relationShips.stream()
                .map(RelationShipEntity::getChild)
                .toList();
        List<MemberEntity> members = relationShips.stream()
                .map(RelationShipEntity::getMember)
                .filter(member -> member.getId().longValue() == memberId)
                .toList();
        relationShipRepository.deleteAll(relationShips);
        memberRepository.deleteAll(members);
        childRepository.deleteAll(children);
    }
}
