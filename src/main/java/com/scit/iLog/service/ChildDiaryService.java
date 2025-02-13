package com.scit.iLog.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.dto.diary.DiaryUpdateDto;
import com.scit.iLog.repository.ChildDiaryRepository;
import com.scit.iLog.repository.ChildRepository;

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

		/*
		2025-02-10 이도훈
		DiaryController의 handleGetDiaryUpdateView메서드에서 diaryId를 찾기 위한 메서드
		*/
		public DiaryUpdateDto getSelectId(Long diaryId) {
			Optional<ChildDiaryEntity> diary = childDiaryRepository.findById(diaryId);
			if(diary.isPresent())
			{
				return DiaryUpdateDto.toDTO(diary.get());
			}
			return null;
		}

	/**
	 * 2025-02-10 이도훈 DiaryController의 handleUpdateDaiaryUpdateView메서드에서 diaryId를 찾은
	 * 후 일기가 수정 되었으면 수정 된 내용을 저장하는 메서드이다.
	 * 
	 * @param updateDto
	 */
	public void updateDiary(DiaryUpdateDto updateDto) {

		// 옵셔널로 감싼 후 Diary테이블의 diary_id조회.
		// 옵셔널로 감싼 이유는 NullPointerExeption null값이 조회되더라도 에러가 발생하지 않음.
		Optional<ChildDiaryEntity> diary = childDiaryRepository.findById(updateDto.id());

		if (!diary.isPresent())
			return;
		// ChildDiaryEntity로 조회한 엔티티를 childDiaryEntity로 저장.
		ChildDiaryEntity childDiaryEntity = diary.get();

		// 조회한 엔티티의 set값을 updateDto의 값으로 변경.
		childDiaryEntity.setId(updateDto.id());
		childDiaryEntity.setContent(updateDto.content());
	}

}
