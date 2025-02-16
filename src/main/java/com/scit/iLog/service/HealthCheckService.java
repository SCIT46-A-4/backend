package com.scit.iLog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.scit.iLog.repository.ChildHealthCheckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.dto.HealthCheckListDTO;
import com.scit.iLog.dto.HealthCheckSaveDTO;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.MemberRepository;
import com.scit.iLog.util.FileManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 영유아 건강 문진(HealthCheck) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 문진 결과 등록, 조회, 삭제 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HealthCheckService {

	private final ChildHealthCheckRepository childHealthCheckRepository;
	private final FileManager fileManager;
	private final ChildRepository childRepository;
	private final MemberRepository memberRepository;

	private static final String UPLOAD_DIR = "/upload/path/"; // 파일 저장 경로

	
	/**
     * 새로운 문진 결과를 저장하는 메서드
     *
     * 사용자가 입력한 문진 데이터를 저장하고, 파일을 업로드한 후 데이터베이스에 반영합니다.
     *
     * @param healthCheckSaveDTO - 저장할 문진 결과 데이터 (아이 ID, 멤버 ID, 파일 포함)
     * @return 저장된 문진 결과의 ID
     * @throws RuntimeException 문진 결과 저장 중 오류 발생 시 예외 발생
     */
	@Transactional
	public Long saveHealthSurvey(HealthCheckSaveDTO healthCheckSaveDTO) {
		try {
			// 1. 아이 및 멤버 정보 조회
			ChildEntity child = childRepository.findById(healthCheckSaveDTO.getChildId()).orElseThrow(
					() -> new IllegalArgumentException("해당 아이 ID가 존재하지 않습니다: " + healthCheckSaveDTO.getChildId()));

			MemberEntity member = memberRepository.findById(healthCheckSaveDTO.getMemberId()).orElseThrow(
					() -> new IllegalArgumentException("해당 멤버 ID가 존재하지 않습니다: " + healthCheckSaveDTO.getMemberId()));

			// 2. 파일 업로드 및 파일명 저장
			String savedFileName = fileManager.saveFile(healthCheckSaveDTO.getSurveyFile(), UPLOAD_DIR);
			String originalFileName = healthCheckSaveDTO.getSurveyFile().getOriginalFilename();

			// 3. 엔티티 저장
			HealthCheckEntity survey = HealthCheckEntity.builder().child(child).member(member)
					.originalSurveyFileName(originalFileName).savedSurveyFileName(savedFileName).build();

			childHealthCheckRepository.save(survey);
			return survey.getId();

		} catch (Exception e) {
			throw new RuntimeException("문진 결과 저장에 실패했습니다.");
		}
	}

	/**
     * 저장된 모든 문진 결과 목록을 조회하는 메서드 (페이징 제외)
     *
     * 데이터베이스에서 모든 문진 결과를 가져와 DTO 리스트로 변환 후 반환합니다.
     *
     * @return 문진 결과 목록 DTO 리스트
     */
	@Transactional(readOnly = true)
	public List<HealthCheckListDTO> getAllHealthCheckList() {
		
		// 데이터베이스에서 모든 문진 결과 조회
		List<HealthCheckEntity> healthSurveyList = childHealthCheckRepository.findAll();
		
		// 변환된 DTO 리스트를 저장할 리스트 생성
		List<HealthCheckListDTO> healthCheckList = new ArrayList<>();
		
		// Entity → DTO 변환
	    for (HealthCheckEntity entity : healthSurveyList) {
	        HealthCheckListDTO dto = HealthCheckListDTO.builder()
	                .id(entity.getId())
	                .childName(entity.getChild().getName())
	                .originalSurveyFileName(entity.getOriginalSurveyFileName())
	                .createdAt(entity.getCreatedAt().toString())
	                .build();

	        healthCheckList.add(dto); // 변환된 DTO를 리스트에 추가
	    }
	    
	    return healthCheckList;
	}
	
	/**
     * 특정 문진 결과를 삭제하는 메서드
     *
     * 문진 결과 ID를 이용해 데이터베이스에서 삭제합니다.
     *
     * @param healthCheckId - 삭제할 문진 결과의 ID
     * @throws IllegalArgumentException 문진 결과가 존재하지 않을 경우 예외 발생
     */
	@Transactional
	public void deleteHealthSurvey(Long healthCheckId) {
		// 1. 문진 결과 조회
		HealthCheckEntity healthSurvey = childHealthCheckRepository.findById(healthCheckId)
				.orElseThrow(() -> new IllegalArgumentException("해당 문진 결과가 존재하지 않습니다: " + healthCheckId));

		// 2. 문진 결과 삭제
		childHealthCheckRepository.delete(healthSurvey);
	}

	// 24/2/10 준성 api-43 영유아 건강문진 결과 삭제요청
	public boolean deleteCheckList(Long id)
	{
		try
		{
			// 25/2/10 준 : 데이터 찾아서 삭제하는 로직
			Optional<HealthCheckEntity> entity = childHealthCheckRepository.findById(id);

			if(entity.isPresent())
			{
				childHealthCheckRepository.delete(entity.get());
				return true;
			}

			return false;
		}

		catch (Exception e)
		{
			log.error("childHealthDelete 삭제 중 에러 발생:" + e.getMessage() + "\n" + e.getCause());
			return false;
		}
}
}