package com.scit.iLog.service;

import java.util.List;
import java.util.stream.Collectors;

import com.scit.iLog.domain.claim.ClaimEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.dto.ClaimsDTO;
import com.scit.iLog.repository.ClaimsRepository;
import com.scit.iLog.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 클레임(문의) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 클레임 등록 및 조회 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j // 로깅 추가
public class ClaimsService {
    private final ClaimsRepository claimsRepository;
    private final MemberRepository memberRepository;

    /**
     * 새로운 클레임(문의)을 저장하는 메서드
     * 
     * 클라이언트에서 전달받은 클레임 정보를 검증하고, 해당 멤버가 존재하는지 확인한 후
     * 데이터베이스에 저장합니다.
     * 
     * @param claimsDTO - 저장할 클레임 정보
     * @return 저장된 클레임 정보를 담은 DTO 객체
     * @throws IllegalArgumentException 멤버 ID가 유효하지 않거나 존재하지 않을 경우 예외 발생
     */
    @Transactional
    public ClaimsDTO saveClaim(ClaimsDTO claimsDTO) {
        // 2️. 멤버 조회
        MemberEntity member = memberRepository.findById(claimsDTO.getAuthorId())
                .orElseThrow(() -> {
                    log.error(" 멤버를 찾을 수 없음: ID {}", claimsDTO.getAuthorId());
                    return new IllegalArgumentException("Invalid member ID");
                });

        // 3.️ DTO → Entity 변환 후 저장
        ClaimEntity claimEntity = ClaimEntity.builder()
                .author(member)
                .title(claimsDTO.getTitle())
                .content(claimsDTO.getContent())
                .build();
        ClaimEntity savedEntity = claimsRepository.save(claimEntity);

        log.info(" 클레임 저장 성공 - ID: {}, 제목: {}", savedEntity.getId(), savedEntity.getTitle());
        
        return ClaimsDTO.toDTO(savedEntity);
    }

    /**
     * 25/2/11 은진
     * 특정 문의를 삭제하는 메서드
     * @param id - 삭제할 문의 id
     */
    @Transactional
    public void deleteClaim(Long id) {
        // 1. 삭제할 문의 조회 (클레임 테이블에서 조회)
        boolean doesClaimExist = claimsRepository.existsById(id);
        // 2. 삭제 실행
        if(doesClaimExist) {
            claimsRepository.deleteById(id);
            log.info("문의 삭제 성공 - ID: {}", id);
        }
    }

    /**
     * 전체 클레임(문의) 목록을 조회하는 메서드
     * 
     * 데이터베이스에서 모든 클레임을 가져와 DTO 리스트로 변환 후 반환합니다.
     * @return 클레임 목록을 담은 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ClaimsDTO> getAllClaims() {
        List<ClaimsDTO> claimsList = claimsRepository.findAll().stream()
                .map(ClaimsDTO::toDTO)
                .collect(Collectors.toList());

        log.info(" 전체 클레임 조회: {}건", claimsList.size());
        return claimsList;
    }

}