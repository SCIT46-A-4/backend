package com.scit.iLog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scit.iLog.domain.ChildDiaryEntity;
import com.scit.iLog.domain.ChildEntity;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.ChildDiaryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildDiaryService
	{
		private final ChildDiaryRepository childDiaryRepository;
		private final ChildRepository childRepository;
		
		// 25/2/6 준성 아이의 id값 주면 그거 기반으로 최근 데이터들 DESC로 paging 해서 메소드
		public Page<ChildDiaryEntity> getChildDiaries(Long id, Pageable page)
		{
			Optional<ChildEntity> child = childRepository.findById(id);
			
			if(child.isPresent())
				{
					return childDiaryRepository.findByChildOrderByCreatedAtDesc(child.get(), page);
				}
			
			return null;
		}
	}
