package com.scit.iLog.service;

import com.scit.iLog.domain.claim.ClaimAnswerEntity;
import com.scit.iLog.domain.claim.ClaimEntity;
import com.scit.iLog.domain.claim.ClaimType;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.member.MemberRole;
import com.scit.iLog.dto.claims.ClaimAnswerDTO;
import com.scit.iLog.dto.claims.ClaimDetailsDTO;
import com.scit.iLog.dto.claims.ClaimListViewDTO;
import com.scit.iLog.dto.claims.ClaimsInsertDTO;
import com.scit.iLog.exception.MemberNotFoundException;
import com.scit.iLog.repository.ClaimAnswerRepository;
import com.scit.iLog.repository.ClaimRepository;
import com.scit.iLog.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * **2024-03-05 ~ 2024-03-09 전제환**
 * <p>
 * **클레임(문의) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.** 클레임 등록, 조회, 삭제, 답변 작성 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final MemberRepository memberRepository;
    private final ClaimAnswerRepository claimAnswerRepository;

    /**
     * **2024-03-05 전제환**
     * <p>
     * ✅ 새로운 문의(클레임)를 저장하는 메서드 클라이언트에서 전달받은 클레임 정보를 검증하고, 해당 멤버가 존재하는지 확인한 후 저장합니다.
     *
     * @param signInId       - 로그인한 사용자 ID
     * @param claimInsertDTO - 저장할 문의 정보
     * @throws IllegalArgumentException - 멤버 ID가 유효하지 않거나 존재하지 않을 경우 예외 발생
     */
    @Transactional
    public void saveClaim(String signInId, ClaimsInsertDTO claimInsertDTO) {

        log.info("📌 saveClaim 호출됨 - 사용자 ID: {}, 제목: {}", signInId, claimInsertDTO.getTitle());

        // 1. 멤버 조회
        MemberEntity member = memberRepository.findBySignInId(signInId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID:" + signInId));

        log.info("✅ 사용자 조회 완료 - ID: {}, 이름: {}", member.getId(), member.getName());

        // 2. 문의 유형 처리
        ClaimType claimType = claimInsertDTO.getType();
        if (claimType == null) {
            log.error("🚨 문의 유형이 null 입니다! 데이터 확인 필요!");
            throw new IllegalArgumentException("문의 유형을 선택해야 합니다.");
        }
        log.info("✅ 문의 유형: {}", claimType);

        // 3. DTO → Entity 변환 후 저장
        ClaimEntity claimEntity = ClaimEntity.builder().author(member).title(claimInsertDTO.getTitle())
                .content(claimInsertDTO.getContent()).type(claimType).build();

        log.info("🚀 Entity 변환 완료 - 제목: {}, 내용: {}", claimEntity.getTitle(), claimEntity.getContent());

        ClaimEntity savedEntity = claimRepository.save(claimEntity);

        log.info("클레임 저장 성공 - ID: {}, 제목: {}", savedEntity.getId(), savedEntity.getTitle());
    }

    /**
     * **2024-03-06 전제환**
     * <p>
     * ✅ 특정 문의를 삭제하는 메서드 존재하지 않는 문의를 삭제하려고 하면 예외가 발생합니다.
     *
     * @param id - 삭제할 문의 ID
     */
    @Transactional
    public void deleteClaim(Long id) {
        // 📌 수정됨: 삭제할 데이터가 없는 경우 예외 발생
        ClaimEntity claim = claimRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 문의가 존재하지 않습니다: ID " + id));

        claimRepository.delete(claim);
    }

    /**
     * **2024-03-07 전제환**
     * <p>
     * ✅ 전체 문의(클레임) 목록을 조회하는 메서드 ADMIN 계정은 모든 문의를 조회할 수 있고, 일반 사용자는 본인이 작성한 문의만
     * 조회합니다.
     *
     * @param signInId - 로그인한 사용자 ID
     * @param pageable - 페이지네이션 정보
     * @return 클레임 목록을 DTO로 변환하여 반환
     */
    @Transactional(readOnly = true)
    public Page<ClaimListViewDTO> getPagedUserClaims(String signInId, Pageable pageable) {

        // 1. 로그인한 사용자 정보 가져오기
        MemberEntity member = memberRepository.findBySignInId(signInId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID: " + signInId));

        // 2. ADMIN이면 모든 글 조회, 일반 사용자는 본인 글만 조회
        Page<ClaimEntity> claims;
        if (member.getRole() == MemberRole.ADMIN) {
            log.info("✅ [서비스] ADMIN 계정 - 모든 문의 조회");
            claims = claimRepository.findAll(pageable); // ✅ 모든 글 조회
        } else {
            log.info("✅ [서비스] 일반 사용자 - 본인 작성 문의만 조회");
            claims = claimRepository.findByAuthor(member, pageable); // ✅ 본인 글만 조회
        }

        // 3. DTO로 변환하여 반환 (✅ 답변 개수 추가)
        return claims.map(claim -> new ClaimListViewDTO(
                claim.getType().getTypeNameKr(),
                claim.getId(),
                claim.getTitle(),
                claim.getCreatedAt(),  // ✅ 엔티티 수정 없이 접근 가능
                claim.getAnswers().size() // ✅ 답변 개수 추가
        ));
    }

    /**
     * **2024-03-08 전제환**
     * <p>
     * ✅ 특정 문의의 상세 정보를 조회하는 메서드
     *
     * @param claimId - 조회할 문의 ID
     * @return 문의 상세 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public ClaimDetailsDTO getClaimDetailsById(Long claimId) {

        // 1. DB에서 해당 문의 찾기
        ClaimEntity claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Claim 조회 실패: %d", claimId)));

        // 2. Entity → DTO 변환 후 반환
        return ClaimDetailsDTO.builder()
                .claimId(claim.getId())
                .type(claim.getType())
                .title(claim.getTitle())
                .createdAt(claim.getCreatedAt()).content(claim.getContent())
                .claimAnswers(claim.getAnswers().stream()
                        .map(claimAnswer -> ClaimAnswerDTO.builder().answerId(claimAnswer.getId())
                                .createdAt(claimAnswer.getCreatedAt()).authorName(claimAnswer.getAuthor().getName())
                                .content(claimAnswer.getContent()).build())
                        .toList())
                .build();
    }

    /**
     * **2024-03-09 전제환**
     * <p>
     * ✅ 관리자가 문의에 대한 답변을 작성하는 메서드 관리자 계정만 답변을 작성할 수 있으며, 답변을 등록하면 문의 상세 페이지에서 확인할 수
     * 있습니다.
     *
     * @param signInId  - 로그인한 관리자 ID
     * @param claimId   - 답변을 달 문의 ID
     * @param answerDTO - 답변 내용 DTO
     */
    @Transactional
    public void saveClaimAnswer(String signInId, Long claimId, ClaimAnswerDTO answerDTO) {
        log.info("📌 [서비스] 답변 저장 시작 - 작성자: {}, 문의 ID: {}, 내용: {}", signInId, claimId, answerDTO.content());

        // 1. 관리자 여부 확인
        MemberEntity admin = memberRepository.findBySignInId(signInId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID: " + signInId));

        if (admin.getRole() != MemberRole.ADMIN) {
            log.error("🚨 [서비스] 관리자 권한 없음 - 사용자 ID: {}", signInId);
            throw new IllegalArgumentException("관리자만 답변을 작성할 수 있습니다.");
        }

        // 2. 문의 찾기
        ClaimEntity claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다. ID: " + claimId));

        // 3. 답변 저장
        ClaimAnswerEntity answer = ClaimAnswerEntity.builder().claim(claim).author(admin).content(answerDTO.content())
                .build();

        claim.addAnswer(answer); // ✅ 양방향 관계 설정
        claimAnswerRepository.save(answer); // ✅ 직접 저장

        log.info("✅ [서비스] 답변 저장 완료 - 답변 ID: {}", answer.getId());
    }

    @Transactional
    public void deleteClaimsAndAnswersOf(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        List<ClaimEntity> allByAuthor = claimRepository.findAllByAuthor(member);
        claimRepository.deleteAll(allByAuthor);
    }
}