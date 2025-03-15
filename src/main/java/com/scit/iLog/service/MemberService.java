package com.scit.iLog.service;

import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.member.MemberRole;
import com.scit.iLog.dto.auth.SignUpDTO;
import com.scit.iLog.dto.member.MemberDashboardProfileDTO;
import com.scit.iLog.dto.member.MemberDetailsDTO;
import com.scit.iLog.dto.member.MemberUpdateDTO;
import com.scit.iLog.exception.MemberNotFoundException;
import com.scit.iLog.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RelationShipRepository relationShipRepository;
    private final ChildDiaryRepository childDiaryRepository;
    private final AnalysisTargetRepository analysisTargetRepository;
    private final GuideRepository guideRepository;
    private final ChildHealthCheckRepository childHealthCheckRepository;

    /**
     * 2025-02-17~20 이도훈
     * 전달받은 memberDTO를 Entity로 변경한 후 save
     *
     * @param signUpDTO
     * @return
     */
    @Transactional
    public void join(SignUpDTO signUpDTO) {
        /**
         * 2025-02-17~20 이도훈
         * 개인정보 수집 이용 동의 필드 추가
         */
        MemberEntity member = memberRepository.save(
                MemberEntity.builder()
                        .signInId(signUpDTO.signInId())
                        .password(passwordEncoder.encode(signUpDTO.userPwd()))
                        .email(signUpDTO.userEmail())
                        .name(signUpDTO.userName())
                        .relationType(signUpDTO.relationType())
                        .role(MemberRole.USER)
                        .personalInformationCollectionAndUsageAgreement(
                                signUpDTO.personalInformationCollectionAndUsageAgreement())
                        .build()
        );
        memberRepository.save(member);
    }

    /**
     * 아이디 중복 검사 메서드
     *
     * @param signInId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean checkSignInIdExists(String signInId) {
        return memberRepository.existsBySignInId(signInId);
    }

    @Transactional(readOnly = true)
    public MemberUpdateDTO getMemberUpdateDataById(Long memberId) {
        MemberEntity member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("멤버 조회 실패: %d", memberId)));
        return MemberUpdateDTO.builder()
                .signInId(member.getSignInId())
                .email(member.getEmail())
                .name(member.getName())
                .relationType(member.getRelationType().getTypeNameKr())
                .personalInformationCollectionAndUsageAgreement(member.isPersonalInformationCollectionAndUsageAgreement())
                .build();
    }

    @Transactional(readOnly = true)
    public MemberDashboardProfileDTO findMemberProfileDataById(String signUpId) {
        MemberEntity member = memberRepository.findBySignInId(signUpId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("대시보드 프로필 데이터 조회 실패: %d", signUpId)));
        return new MemberDashboardProfileDTO(member.getName(), member.getRelationType());
    }

    @Transactional
    public void inValidateMember(Long memberId) {
        // 1. 삭제할 MemberEntity를 조회 (없으면 예외 발생)
        MemberEntity member = findById(memberId);

        member.setRole(MemberRole.LEAVED);
    }

    @Transactional(readOnly = true)
    public MemberDetailsDTO getMemberDetailsById(Long memberId) {
        MemberEntity member = findById(memberId);
        return MemberDetailsDTO.builder()
                .signInId(member.getSignInId())
                .email(member.getEmail())
                .name(member.getName())
                .relationType(member.getRelationType() != null ? member.getRelationType().getTypeNameKr() : "관리자")
                .personalInformationCollectionAndUsageAgreement(member.isPersonalInformationCollectionAndUsageAgreement())
                .build();
    }

    public MemberEntity findById(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("회원 조회 실패: %d", memberId)));
        return member;
    }

    @Transactional
    public void updateMember(Long memberId, String email, String newPassword) {
        MemberEntity member = findById(memberId);
        member.setPassword(passwordEncoder.encode(newPassword));

        if (member.getEmail().equals(email)) return;
        member.setEmail(email);
    }

    @Transactional
    public void deleteMemberWithRelationShips(Long memberId) {
        MemberEntity member = findById(memberId);
        List<RelationShipEntity> relationships = member.getRelationShips();
        if (relationships != null && !relationships.isEmpty()) {
            relationships.forEach(relationShip -> relationShip.setMember(null));
            relationShipRepository.deleteAll(relationships);
        }
        memberRepository.delete(member);
    }

    @Transactional
    public void deleteMemberById(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    public boolean isDuplicatedPassword(Long memberId, String password) {
        MemberEntity member = findById(memberId);
        return passwordEncoder.matches(password, member.getPassword());
    }

    /**
     * 2025-03-11~12
     * <p>
     * ✅ 이메일을 통해 아이디 찾기
     * 사용자가 입력한 이메일로 등록된 계정을 찾고, 해당 계정의 아이디를 반환합니다.
     *
     * @param email - 사용자가 입력한 이메일
     * @return 찾은 회원의 아이디 (signInId)
     * @throws MemberNotFoundException - 이메일로 등록된 계정이 없을 경우 예외 발생
     */
    @Transactional
    public String getSignInIdByEmail(String email) {
        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(email));

        return member.getSignInId();
    }

    /**
     * 2025-03-11~12
     * <p>
     * ✅ 아이디와 이메일이 일치하는지 확인
     * 사용자가 입력한 아이디와 이메일이 실제로 같은 계정인지 확인합니다.
     *
     * @param email    - 사용자가 입력한 이메일
     * @param signInId - 사용자가 입력한 아이디
     * @return 일치하면 true, 일치하지 않으면 false 반환
     * @throws MemberNotFoundException - 아이디로 등록된 계정이 없을 경우 예외 발생
     */
    @Transactional
    public boolean checkMemberEmail(String email, String signInId) {
        MemberEntity member = memberRepository.findBySignInId(signInId)
                .orElseThrow(() -> new MemberNotFoundException(signInId));
        return member.getEmail().equals(email);
    }

    /**
     * 2025-03-11~12 전제환
     * <p>
     * ✅ 비밀번호 초기화 및 임시 비밀번호 발급
     * 사용자가 입력한 아이디를 확인한 후, 새로운 임시 비밀번호를 생성하고 저장합니다.
     *
     * @param signInId - 사용자가 입력한 아이디
     * @return 새롭게 생성된 임시 비밀번호
     * @throws MemberNotFoundException - 아이디로 등록된 계정이 없을 경우 예외 발생
     */
    @Transactional
    public String resetPassword(String signInId) {
        MemberEntity member = memberRepository.findBySignInId(signInId)
                .orElseThrow(() -> new MemberNotFoundException(signInId));

        // 랜덤한 새 비밀번호 생성
        String newPassword = generateRandomPassword();
        // 생성된 비밀번호를 암호화하여 저장
        member.setPassword(passwordEncoder.encode(newPassword));
        // 임시 비밀번호 반환 (사용자가 이메일에서 확인 가능)
        return newPassword;
    }

    /**
     * 2025-03-11~12 전제환
     * <p>
     * ✅ 5~12자의 랜덤 비밀번호 생성
     * - 영문 대문자, 소문자, 숫자, 특수문자를 포함해야 함
     * - 최소 5자 ~ 최대 12자 길이의 비밀번호를 랜덤으로 생성
     *
     * @return 조건을 충족하는 랜덤한 비밀번호 문자열
     */
    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        String allCharacters = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;

        // 5~12 길이 랜덤 선택
        int passwordLength = random.nextInt(8) + 5;

        StringBuilder password = new StringBuilder();

        // ✅ 필수 문자 추가 (최소 1개씩)
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // ✅ 나머지 랜덤 문자 채우기
        for (int i = 4; i < passwordLength; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        // ✅ 비밀번호 랜덤 섞기
        return shuffleString(password.toString());
    }

    /**
     * 2025-03-11~12 전제환
     * <p>
     * ✅ 문자열을 랜덤으로 섞는 메서드
     * 비밀번호 생성 시, 필수 문자가 포함된 상태에서 전체 문자열의 순서를 랜덤으로 변경하여 보안성을 높입니다.
     *
     * @param input - 랜덤 비밀번호 (초기 순서)
     * @return 섞인 상태의 비밀번호 문자열
     */
    private String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }

        Collections.shuffle(characters, new Random());
        StringBuilder shuffled = new StringBuilder();
        for (char c : characters) {
            shuffled.append(c);
        }

        return shuffled.toString();
    }
}
