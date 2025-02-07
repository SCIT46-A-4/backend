package com.scit.iLog.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.dto.MemberDTO;
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
	 * @param memberDTO
	 * @return
	 */
	public void join(MemberDTO memberDTO) {
		memberRepository.save(
				MemberEntity.builder()
						.userId(memberDTO.getUserId())
						.password(passwordEncoder.encode(memberDTO.getPassword()))
						.email(memberDTO.getEmail())
						.build()
		);
	}
}
