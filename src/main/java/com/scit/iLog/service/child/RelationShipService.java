package com.scit.iLog.service.child;

import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.repository.MemberRepository;
import com.scit.iLog.repository.RelationShipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelationShipService {
    private final RelationShipRepository relationShipRepository;
    private final MemberRepository memberRepository;

    public void deleteAllChildrenOf(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("모든 아동 삭제시 회원 조회 실패: %d", memberId)));
        relationShipRepository.deleteByMember(member);
    }
}
