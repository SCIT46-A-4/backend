package com.scit.iLog.service;

import com.scit.iLog.domain.*;
import com.scit.iLog.dto.MyPageDTO;
import com.scit.iLog.dto.auth.SignUpDTO;
import com.scit.iLog.repository.RelationShipRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scit.iLog.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final RelationShipRepository relationShipRepository;

	/**
	 * 전달받은 memberDTO를 Entity로 변경한 후 save
	 * @param signUpDTO
	 * @return
	 */
	public void join(SignUpDTO signUpDTO) {
		// @TODO 부족한 필드 추가. 휴대폰 번호 등
		MemberEntity member = memberRepository.save(
				MemberEntity.builder()
						.signInId(signUpDTO.signInId())
						.password(passwordEncoder.encode(signUpDTO.userPwd()))
						.email(signUpDTO.userEmail())
						.name(signUpDTO.userName())
						.role(MemberRole.USER)
						.build()
		);
	}

	public boolean checkSignInIdExists(String signInId) {
		return memberRepository.existsBySignInId(signInId);
	}

	public MyPageDTO findById(Long memberId) {
		MemberEntity member = memberRepository
				.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("멤버 조회 실패: %d", memberId)));
		return MyPageDTO.builder()
				.signInId(member.getSignInId())
				.userEmail(member.getEmail())
				.userName(member.getName())
				.build();
	}
}
