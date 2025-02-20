package com.scit.iLog.service;

import com.scit.iLog.domain.GuideEntity;
import com.scit.iLog.dto.guide.GuideDTO;
import com.scit.iLog.repository.GuideRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideService
	{
		private final GuideRepository guideRepository;

		// 모든 이용안내 목록 가져오기
		public List<GuideEntity> getAllGuides()
			{
				return guideRepository.findAll();
			}

		public List<GuideDTO> selectAll(// Pageable pageable,
				String searchItem, String searchWord)
			{
				// 1) -1: DB의 [age의 위치의 값은 0부터시작하고, 사용자에게는 0페이지가 없이 1페이지를 요청하기 때문에
				// 사용자가 1페이지를 요청하면 DB에서 0 페이지의 데이터를 가져와야 한다.
				// int page = pageable.getPageNumber()-1;

				// 2) 페이징이 추가된 조회
				// Page<GuideEntity> temp = null;
			
				// 3) 대소문자 구분없이 검색가능한 기능 구현
				//findByTitleContainingIgnoreCase, findByContentContainingIgnoreCase 사용
				List<GuideEntity> guideEntityList = null;

				switch (searchItem)
					{
					case "title":
						guideEntityList = guideRepository.findByTitleContainingIgnoreCase(searchWord);
						break;

					case "content":
						guideEntityList = guideRepository.findByContentContainingIgnoreCase(searchWord);
						break;
					}

				// Page<GuideDTO> list = null;
				List<GuideDTO> GuideDTOList = new ArrayList<>();

				// 람다식, 스트림(목록을 가지고 변경(map)할 때 주로 사용)
				// map을 이용해서 DTO 생성자를 이용해 목록을 만들겠다.
				for (var t : guideEntityList)
					{
						GuideDTO _data = GuideDTO.builder()
								.id(t.getId())
								.title(t.getTitle())
								.content(t.getContent())
								.build();

						GuideDTOList.add(_data);
					}

				return GuideDTOList;
			}
		


	}
