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

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationShipService {
    private final RelationShipRepository relationShipRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;

    public void deleteAllRelationShipOf(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("모든 아동 삭제시 회원 조회 실패: %d", memberId)));
        List<RelationShipEntity> relationShips = relationShipRepository.findAllByMemberId(memberId);
        List<ChildEntity> children = relationShips.stream()
                .map(relationShip -> relationShip.getChild())
                .toList();
        /*
            @TODO child와 관련된 엔티티가 전부 잘 삭제 되는지 확인 필요
         */
        relationShipRepository.deleteByMember(member);
        childRepository.deleteAll(children);
    }
}
