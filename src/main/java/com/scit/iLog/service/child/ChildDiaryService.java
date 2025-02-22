package com.scit.iLog.service.child;

import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.dto.diary.DiaryDetailsDTO;
import com.scit.iLog.dto.diary.DiaryUpdateDto;
import com.scit.iLog.repository.ChildDiaryRepository;
import com.scit.iLog.repository.ChildRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildDiaryService {
		private final ChildDiaryRepository childDiaryRepository;
		private final ChildRepository childRepository;
		
		// 25/2/6 준성 아이의 id값 주면 그거 기반으로 최근 데이터들 DESC로 paging 해서 메소드
		public Page<ChildDiaryEntity> getChildDiaries(Long childId, Pageable page) {
			ChildEntity child = childRepository.findById(childId)
					.orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));
			return childDiaryRepository.findByChildOrderByCreatedAtDesc(child, page);
		}

		/*
		2025-02-10 이도훈
		DiaryController의 handleGetDiaryUpdateView메서드에서 diaryId를 찾기 위한 메서드
		*/
		@Transactional(readOnly = true)
		public DiaryUpdateDto findDiaryUpdateInfoById(Long diaryId) {
			ChildDiaryEntity diary = findChildDiaryById(diaryId);

			return DiaryUpdateDto.builder()
						.id(diary.getId())
						.title(diary.getTitle())
						.content(diary.getContent())
						.createdAt(diary.getCreatedAt())
						.build();
		}

		/**
		 * 2025-02-10 이도훈 DiaryController의 handleUpdateDaiaryUpdateView메서드에서 diaryId를 찾은
		 * 후 일기가 수정 되었으면 수정 된 내용을 저장하는 메서드이다.
		 *
		 * @param diaryId
		 * @param updateDto
		 */
		@Transactional
		public void updateDiary(Long diaryId, DiaryUpdateDto updateDto) {
			ChildDiaryEntity diary = findChildDiaryById(diaryId);

			// ChildDiaryEntity로 조회한 엔티티를 childDiaryEntity로 저장.
			diary.setContent(updateDto.content());
			diary.setTitle(updateDto.title());
		}


	public DiaryDetailsDTO findDiaryDetailsById(Long diaryId) {
		ChildDiaryEntity diary = findChildDiaryById(diaryId);
		return DiaryDetailsDTO.builder()
					.title(diary.getTitle())
					.content(diary.getContent())
					.createdAt(diary.getCreatedAt())
					.build();
		}

	private ChildDiaryEntity findChildDiaryById(Long diaryId) {
		ChildDiaryEntity diary = childDiaryRepository.findById(diaryId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Diary 조회 실패: %d", diaryId)));
		return diary;
	}
}
