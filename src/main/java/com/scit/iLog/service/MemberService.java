package com.scit.iLog.service;

import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.member.MemberRole;
import com.scit.iLog.dto.auth.SignUpDTO;
import com.scit.iLog.dto.member.MemberDashboardProfileDTO;
import com.scit.iLog.dto.member.MemberDetailsDTO;
import com.scit.iLog.dto.member.MemberUpdateDTO;
import com.scit.iLog.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
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

	private MemberEntity findById(Long memberId) {
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
}
