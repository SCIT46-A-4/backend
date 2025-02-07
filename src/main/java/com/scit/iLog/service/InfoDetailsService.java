package com.scit.iLog.service;

import java.util.Optional;

import com.scit.iLog.dto.child.ChildDetailsDto;
import org.springframework.stereotype.Service;

import com.scit.iLog.domain.ChildEntity;
import com.scit.iLog.repository.ChildRepository;

import lombok.RequiredArgsConstructor;

/*2025-02-06 이도훈 아이 정보 서비스 생성 */

@Service
@RequiredArgsConstructor
public class InfoDetailsService {
	/*2025-02-06 이도훈 ChildRepository선언 */
	private final ChildRepository childRepository;

	/*2025-02-06 이도훈 infoDetails에 출력할 아이 한명의 데이터 조회 */
	public ChildDetailsDto seletInfoDetails(Long id) {
		Optional<ChildEntity> temp = childRepository.findById(id);
		
		if(temp.isPresent()) {
			return ChildDetailsDto.toDTO(temp.get());
		}
		return null;
	}
}
