package com.scit.iLog.service;

import com.scit.iLog.domain.MemberRole;
import com.scit.iLog.dto.auth.SignUpDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 전달받은 memberDTO를 Entity로 변경한 후 save
	 * @param signUpDTO
	 * @return
	 */
	public void join(SignUpDTO signUpDTO) {
		// @TODO 부족한 필드 추가. 휴대폰 번호 등
		memberRepository.save(
				MemberEntity.builder()
						.signInId(signUpDTO.signInId())
						.password(passwordEncoder.encode(signUpDTO.userPwd()))
						.email(signUpDTO.userEmail())
						.name(signUpDTO.userName())
						.role(MemberRole.USER)
						.build()
		);
	}
}
