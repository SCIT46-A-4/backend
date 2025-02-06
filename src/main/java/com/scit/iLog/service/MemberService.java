package com.scit.iLog.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.dto.MemberDTO;
import com.scit.iLog.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
	private final MemberRepository memberRepository;
//	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * 전달받은 memberDTO를 Entity로 변경한 후 save
	 * @param memberDTO
	 * @return
	 */
	public boolean joinProc(MemberDTO memberDTO) {
		// TODO Auto-generated method stub
		
//		memberDTO.setPassword(bCryptPasswordEncoder.encode(memberDTO.getPassword()));
		
		MemberEntity entity = MemberEntity.toEntity(memberDTO);
		memberRepository.save(entity);	// 회원가입 완료
		
		boolean result =  memberRepository.existsById(memberDTO.getId());
		
		return result;
	}
}
