package com.scit.iLog.service;

import java.util.List;

import com.scit.iLog.domain.HelpEntity;
import com.scit.iLog.repository.HelpRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HelpService {

	private final HelpRepository helpRepository;
	
	// 모든 이용안내 목록 가져오기
	public List<HelpEntity> getAllGuides() {
        return helpRepository.findAll();
    }
}
