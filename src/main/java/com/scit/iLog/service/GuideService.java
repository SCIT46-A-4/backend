package com.scit.iLog.service;

import java.util.List;

import com.scit.iLog.domain.GuideEntity;
import com.scit.iLog.repository.GuideRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuideService {

	private final GuideRepository guideRepository;
	
	// 모든 이용안내 목록 가져오기
	public List<GuideEntity> getAllGuides() {
		return guideRepository.findAll();
    }
}
