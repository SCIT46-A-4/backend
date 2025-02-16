package com.scit.iLog.service.child;

import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.dto.diary.DiaryUpdateDto;
import com.scit.iLog.repository.ChildDiaryRepository;
import com.scit.iLog.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
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
		@Transactional(readOnly = true)
		public DiaryUpdateDto findDiaryById(Long diaryId) {
			/*
				@TODO
				조회 실패시 에러 던집시다!!!
			 */
			Optional<ChildDiaryEntity> diary = childDiaryRepository.findById(diaryId);
			if(diary.isPresent())
			{
				return DiaryUpdateDto.builder()
						.id(diary.get().getId())
						.title(diary.get().getTitle())
						.content(diary.get().getContent())
						.createdAt(diary.get().getCreatedAt())
						.build();
			}
			/*
				@TODO
				이렇게 null 반환하면 안됩니다!!!
			 */
			return null;
		}

	/**
	 * 2025-02-10 이도훈 DiaryController의 handleUpdateDaiaryUpdateView메서드에서 diaryId를 찾은
	 * 후 일기가 수정 되었으면 수정 된 내용을 저장하는 메서드이다.
	 *
	 * @param diaryId
	 * @param updateDto
	 */
	/*
		@TODO 그리고 이렇게 dto에 파라미터가 2개밖에 안되는 경우에는 그냥 개별 값을
		dto에서 꺼내서 넣어주는것이 좋습니다!
	 */
	@Transactional
	public void updateDiary(Long diaryId, DiaryUpdateDto updateDto) {

		// 옵셔널로 감싼 후 Diary테이블의 diary_id조회.
		// 옵셔널로 감싼 이유는 NullPointerExeption null값이 조회되더라도 에러가 발생하지 않음.
		/*
			@TODO 조회 실패시 명확하게 에러 던집시다!
		 */
		Optional<ChildDiaryEntity> diary = childDiaryRepository.findById(diaryId);

		if (!diary.isPresent())
			return;
		// ChildDiaryEntity로 조회한 엔티티를 childDiaryEntity로 저장.
		ChildDiaryEntity childDiaryEntity = diary.get();

		// 조회한 엔티티의 set값을 updateDto의 값으로 변경.
		/*
			@TODO id는 업데이트하는거 아닙니다!!!
		 */
//		childDiaryEntity.setId(updateDto.id());
		childDiaryEntity.setContent(updateDto.content());
		childDiaryEntity.setTitle(updateDto.title());
	}
}
