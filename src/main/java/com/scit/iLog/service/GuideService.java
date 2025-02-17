package com.scit.iLog.service;

import com.scit.iLog.domain.GuideEntity;
import com.scit.iLog.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideService {

	private final GuideRepository guideRepository;
	
	// 모든 이용안내 목록 가져오기
	public List<GuideEntity> getAllGuides() {
		return guideRepository.findAll();
    }
}
