package com.scit.iLog.service.child;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.dto.diary.ChildDiaryInsertDTO;
import com.scit.iLog.dto.diary.DiaryDetailsDTO;
import com.scit.iLog.dto.diary.DiaryUpdateDTO;
import com.scit.iLog.dto.diary.DiaryUpdateRequestDTO;
import com.scit.iLog.exception.ChildNotFoundException;
import com.scit.iLog.exception.MemberNotFoundException;
import com.scit.iLog.repository.ChildDiaryRepository;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildDiaryService {
	private final ChildDiaryRepository childDiaryRepository;
	private final ChildRepository childRepository;
	private final MemberRepository memberRepository;

	// 25/2/6 준성 아이의 id값 주면 그거 기반으로 최근 데이터들 DESC로 paging 해서 메소드
	public Page<ChildDiaryEntity> getChildDiaries(Long childId, Pageable page) {
		ChildEntity child = childRepository.findById(childId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));
		return childDiaryRepository.findByChildOrderByCreatedAtDesc(child, page);
	}

	/*
	 * 2025-02-10 이도훈 DiaryController의 handleGetDiaryUpdateView메서드에서 diaryId를 찾기 위한
	 * 메서드
	 */
	@Transactional(readOnly = true)
	public DiaryUpdateDTO findDiaryUpdateInfoById(Long diaryId) {
		ChildDiaryEntity diary = findChildDiaryById(diaryId);

		return DiaryUpdateDTO.builder().id(diary.getId()).childId(diary.getChild().getId()).title(diary.getTitle()).content(diary.getContent())
				.createdAt(diary.getCreatedAt()).build();
	}

	/**
	 * 2025-02-10 이도훈 DiaryController의 handleUpdateDaiaryUpdateView메서드에서 diaryId를 찾은
	 * 후 일기가 수정 되었으면 수정 된 내용을 저장하는 메서드이다.
	 *
	 * @param diaryId
	 */
	@Transactional
	public void updateDiary(Long diaryId, DiaryUpdateRequestDTO diaryUpdateRequestDTO) {
		ChildDiaryEntity diary = findChildDiaryById(diaryId);

		// ChildDiaryEntity로 조회한 엔티티를 childDiaryEntity로 저장.
		diary.setContent(diaryUpdateRequestDTO.content());
		diary.setTitle(diaryUpdateRequestDTO.title());
	}

	public DiaryDetailsDTO findDiaryDetailsById(Long diaryId) {
		ChildDiaryEntity diary = findChildDiaryById(diaryId);
		return DiaryDetailsDTO.builder().id(diary.getId()).childId(diary.getChild().getId()).title(diary.getTitle()).content(diary.getContent())
				.createdAt(diary.getCreatedAt()).build();
	}

	private ChildDiaryEntity findChildDiaryById(Long diaryId) {
		return childDiaryRepository.findById(diaryId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Diary 조회 실패: %d", diaryId)));
	}

	@Transactional
	public Long saveDiary(Long childId, Long memberId, ChildDiaryInsertDTO childDiaryInsertDTO) {
		ChildEntity child = childRepository.findById(childId).orElseThrow(() -> new ChildNotFoundException(childId));
		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberNotFoundException(memberId));
		return childDiaryRepository.save(ChildDiaryEntity.builder().title(childDiaryInsertDTO.title()).author(member)
				.content(childDiaryInsertDTO.content()).child(child).build()).getId();
	}

	@Transactional
	public void inValidateByMember(Long memberId) {
		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberNotFoundException(memberId));
		childDiaryRepository.findAllByAuthor(member).forEach(childDiary -> childDiary.setAuthor(null));
	}

	public boolean existsDiary(Long diaryId) {
		return childDiaryRepository.existsById(diaryId);
	}

	@Transactional
	public void deleteDiary(Long diaryId) {
		if (!childDiaryRepository.existsById(diaryId)) {
			throw new IllegalArgumentException("삭제할 일기가 존재하지 않습니다: " + diaryId);
		}
		childDiaryRepository.deleteById(diaryId);
	}

}
