package com.scit.iLog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scit.iLog.domain.GuideEntity;
import com.scit.iLog.repository.GuidesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuidesService {

	private final GuidesRepository guidesRepository;
	
	// 모든 이용안내 목록 가져오기
	public List<GuideEntity> getAllGuides() {
        return guidesRepository.findAll();
    }
}
