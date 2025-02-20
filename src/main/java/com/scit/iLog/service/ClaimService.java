package com.scit.iLog.service;

import com.scit.iLog.domain.claim.ClaimEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.dto.ClaimDetailsDTO;
import com.scit.iLog.dto.claims.ClaimAnswerDTO;
import com.scit.iLog.dto.claims.ClaimListViewDTO;
import com.scit.iLog.dto.claims.ClaimsAndAnswersDTO;
import com.scit.iLog.dto.claims.ClaimsInsertDTO;
import com.scit.iLog.repository.ClaimRepository;
import com.scit.iLog.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 클레임(문의) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 클레임 등록 및 조회 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimService {
    private final ClaimRepository claimsRepository;
    private final MemberRepository memberRepository;

    /**
     * 새로운 클레임(문의)을 저장하는 메서드
     * 클라이언트에서 전달받은 클레임 정보를 검증하고, 해당 멤버가 존재하는지 확인한 후
     * 데이터베이스에 저장합니다.
     *
     * @param signInId
     * @param claimInsertDTO - 저장할 클레임 정보
     * @return 저장된 클레임 정보를 담은 DTO 객체
     * @throws IllegalArgumentException 멤버 ID가 유효하지 않거나 존재하지 않을 경우 예외 발생
     */
    @Transactional
    public void saveClaim(String signInId, ClaimsInsertDTO claimInsertDTO) {
        /*
            현재 불필요한 검증 로직 지웁니다.
            검증은 따로 잘 처리하는 방법이 있습니다. - 호준
         */
        // 2️. 멤버 조회
        MemberEntity member = memberRepository.findById(Long.valueOf(signInId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID:" + signInId));

        // 3.️ DTO → Entity 변환 후 저장
        ClaimEntity claimEntity = ClaimEntity.builder()
                .author(member)
                .title(claimInsertDTO.getTitle())
                .content(claimInsertDTO.getContent())
                .build();
        ClaimEntity savedEntity = claimsRepository.save(claimEntity);

        log.info("클레임 저장 성공 - ID: {}, 제목: {}", savedEntity.getId(), savedEntity.getTitle());
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
    public ClaimsAndAnswersDTO getAllClaimsAndAnswers() {
        /*
            고객 문의 조회할 때 답변도 같이 조회하도록 수정합니다. - 호준
         */
        List<ClaimListViewDTO> claimsList = claimsRepository
                .findAll().stream()
                .map(claim ->
                        ClaimListViewDTO.builder()
                                .claimId(claim.getId())
                                .authorName(claim.getAuthor().getName())
                                .title(claim.getTitle())
                                .content(claim.getContent())
                                .type(claim.getType().toString())
                                .claimAnswers(
                                        claim.getAnswers().stream()
                                                .map(claimAnswer ->
                                                        ClaimAnswerDTO.builder()
                                                                .answerId(claimAnswer.getId())
                                                                .title(claimAnswer.getTitle())
                                                                .authorName(claimAnswer.getAuthor().getName())
                                                                .content(claimAnswer.getContent())
                                                                .build()
                                                )
                                                .toList()
                                )
                                .build())
                .toList();
        log.info(" 전체 클레임 조회: {}건", claimsList.size());
        return new ClaimsAndAnswersDTO(claimsList.size(), claimsList);
    }

    public ClaimDetailsDTO getClaimDetailsById(Long claimId) {
        ClaimEntity claim = claimsRepository.findById(claimId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Claim 조회 실패: %d", claimId)));
        return ClaimDetailsDTO.builder()
                .claimId(claim.getId())
                .authorName(claim.getAuthor().getName())
                .type(claim.getType())
                .title(claim.getTitle())
                .content(claim.getContent())
                .claimAnswers(
                        claim.getAnswers().stream()
                                .map(claimAnswer ->
                                        ClaimAnswerDTO.builder()
                                                .answerId(claimAnswer.getId())
                                                .title(claimAnswer.getTitle())
                                                .authorName(claimAnswer.getAuthor().getName())
                                                .content(claimAnswer.getContent())
                                                .build()
                                )
                                .toList()
                )
                .build();
    }
}