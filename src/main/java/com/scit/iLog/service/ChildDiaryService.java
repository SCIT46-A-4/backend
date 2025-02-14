package com.scit.iLog.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.dto.child.ChildSelectIdDto;
import com.scit.iLog.dto.diary.DiaryCreateDto;
import com.scit.iLog.dto.diary.DiaryIdSelectDto;
import com.scit.iLog.dto.diary.DiaryUpdateDto;
import com.scit.iLog.repository.ChildDiaryRepository;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildDiaryService
	{
		private final ChildDiaryRepository childDiaryRepository;
		private final ChildRepository childRepository;
		private final MemberRepository memberRepository;

		/**
		 * API-43
		 * 2025-02-11~13 이도훈
		 * 메서드명 getChildDiaries -> findChildDiaries
		 * 타입을 ChildEntity -> ChildDiaryEntity로 변경.
		 * @param id
		 * @param page
		 * @return
		 */
		@Transactional(readOnly = true)
		public Page<ChildDiaryEntity> selectChildDiaries(Long id, Pageable page)
		{
			Optional<ChildEntity> child = childRepository.findById(id);
			
			if(child.isPresent())
			{
				return childDiaryRepository.findByChildOrderByCreatedAtDesc(child.get(), page);
			}
			
			return null;
		}

		/**
		 * API-44
		 * 2025-02-11~13 이도훈
		 * @param diaryId
		 */
		@Transactional
		public void deleteDiary(Long diaryId) {
		    if (!childDiaryRepository.existsById(diaryId)) {
		        throw new IllegalArgumentException("삭제할 일기가 존재하지 않습니다: " + diaryId);
		    }
		    childDiaryRepository.deleteById(diaryId);
		}
		/**
		 * API-44
		 * @param diaryId
		 * @return
		 */
		public boolean existsDiary(Long diaryId) {
			return childDiaryRepository.existsById(diaryId);
		}
		/**
		 * API-45
		 * 2025-02-11~13 이도훈
		 * @param childId
		 * @return
		 */
		@Transactional(readOnly = true)
		public ChildSelectIdDto selectChildId(Long childId) {
			// 아이 정보 조회
			ChildEntity childEntity = childRepository
					.findById(childId)
					.orElseThrow(() -> new EntityNotFoundException("해당 아이 조회 실패"));
			// ChildSelectIdDto에 아이 정보와 AuthId 포함
			return ChildSelectIdDto.builder()
					.id(childEntity.getId())
					.name(childEntity.getName())
//					.authId("3")
//					.authId(childEntity.get) .authId는 memberEntity에서 갖고 와야 함.
					.build();
		}

		/**
		 * API-46
		 * 2025-02-11~13 이도훈
		 * @param childId
		 * @param authorId
		 * @param diaryCreateDto
		 */
		@Transactional
		public void insertDiary(Long childId, Long authorId, DiaryCreateDto diaryCreateDto) {

			ChildEntity childEntity = childRepository
					.findById(childId)
					.orElseThrow(() -> new EntityNotFoundException("조회 실패"));

			// 해당 authorId로 MemberEntity 조회
			MemberEntity memberEntity = memberRepository.findById(authorId)
					.orElseThrow(() -> new IllegalArgumentException("사용자 조회 실패"));

			// ✅ Builder 패턴을 이용한 객체 생성
			ChildDiaryEntity childDiaryEntity = ChildDiaryEntity.builder()
					.child(childEntity)
					.author(memberEntity)
					.content(diaryCreateDto.content())
					.createdAt(LocalDateTime.now())
					.build();

			childDiaryRepository.save(childDiaryEntity);
		}
		/**
		 * API-47
		 * 2025-02-11~13 이도훈
		 * @param id
		 * @return
		 */
		@Transactional(readOnly = true)
		public DiaryIdSelectDto selectChildDiaryId(Long id) {

			ChildDiaryEntity diaryEntity = childDiaryRepository
					.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("해당 일기 조회 실패"));

			return DiaryIdSelectDto
					.builder()
					.id(diaryEntity.getId())
					.build();
		}
		/**
		 * API-48
		 * 2025-02-11~13 이도훈
		 * @param id
		 * @return
		 */
		@Transactional(readOnly = true)
		public DiaryUpdateDto selectUpdateChildDiary(Long id) {

			System.out.println("================= selectUpdateChildDiary");
			System.out.println(id);
			ChildDiaryEntity diaryEntity = childDiaryRepository
					.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("해당 일기 조회 실패"));

			return DiaryUpdateDto
			.builder()
			.id(diaryEntity.getId())
			.content(diaryEntity.getContent())
			.build();
		}

		/**
		 * API-49
		 * 2025-02-11 이도훈
		 * @param diaryId
		 * @param updateDto
		 */
		@Transactional
		public void updateDiaryDetail(Long diaryId, DiaryUpdateDto updateDto) {
			System.out.println(diaryId);
			ChildDiaryEntity diaryEntity = childDiaryRepository
					.findById(diaryId)
					.orElseThrow(() -> new EntityNotFoundException("해당 일기 조회 실패"));

			diaryEntity.setContent(updateDto.content());
		}
		/**
		 * API-44, API-49
		 * @param diaryId
		 * @return
		 */
		@Transactional
		public Long updateDiarySelectChild(Long diaryId) {
			 // diaryId로 ChildDiaryEntity 조회
		    ChildDiaryEntity childDiaryEntity = childDiaryRepository.findById(diaryId)
		            .orElseThrow(() -> new EntityNotFoundException("해당 일기 조회 실패"));

		    // 해당 일기와 연관된 ChildEntity의 id 반환
		    return childDiaryEntity.getChild().getId();
		}
	}