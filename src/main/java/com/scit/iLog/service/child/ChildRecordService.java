package com.scit.iLog.service.child;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.child.ChildRecordEntity;
import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.dto.child.ChildRecordDTO;
import com.scit.iLog.dto.child.ChildRecordInsertDTO;
import com.scit.iLog.dto.child.ChildRecordListItemDTO;
import com.scit.iLog.dto.child.ChildRecordUpdateRequestDTO;
import com.scit.iLog.dto.child.ChildRecordUpdateViewDTO;
import com.scit.iLog.repository.ChildHealthCheckRepository;
import com.scit.iLog.repository.ChildRecordRepository;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.MemberRepository;
import com.scit.iLog.util.FileManager;
import com.scit.iLog.util.FilePathUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;

import static com.scit.iLog.config.WebConfig.HEALTHCHECK_IMAGES_REQUEST_ROOT_PATH;

@Service
@RequiredArgsConstructor
public class ChildRecordService {
    private final ChildRepository childRepository;
    private final ChildRecordRepository childRecordRepository;
    private final MemberRepository memberRepository;
    private final ChildHealthCheckRepository healthCheckRepository;
    private final FileManager fileManager;
    private final FilePathUtil filePathUtil;

    public ChildRecordDTO findOneByChildIdAndRecordId(Long childId, Long recordId) {
        ChildRecordEntity childRecord = childRecordRepository.findByChildIdAndRecordId(childId, recordId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("아이 신체정보 조회실패: %d /%d",childId,recordId)));
        return ChildRecordDTO.builder()
                .registerDate(childRecord.getRegisterDate())
                .note(childRecord.getNote())
                .height(childRecord.getHeight())
                .weight(childRecord.getWeight())
                .leftEye(childRecord.getLeftEye())
                .rightEye(childRecord.getRightEye())
                .healthCheckImageSrc(
                        HEALTHCHECK_IMAGES_REQUEST_ROOT_PATH
                                .concat(
                                        childRecord
                                                .getHealthCheck()
                                                .getSavedFileName())
                )
                .build();
    }

    /**
     * Controller : handleGetChildRecordListView
     * API : v1.x.x-3
     * C-6 이도훈 2025-02-24~26
     * @param childId  아이의 ID
     * @param pageable 페이징 정보
     * @return 페이징된 ChildRecordListItemDTO 목록
     */
    public Page<ChildRecordListItemDTO> findPagedChildRecords(Long childId, Pageable pageable) {
        return childRecordRepository.findByChildId(childId, pageable)
                .map(childRecord ->
                        ChildRecordListItemDTO.builder()
                                .id(childRecord.getId())
                                .note(childRecord.getNote())
                                .registerDate(childRecord.getRegisterDate())
                                .height(childRecord.getHeight())
                                .weight(childRecord.getWeight())
                                .hasHealthCheckImg(!ObjectUtils.isEmpty(childRecord.getHealthCheck()))
                                .build()
                );
    }

	/**
	 * C-6 이도훈 2025-02-24~26
	 *
	 * @param recordId
	 */
	@Transactional
	public void deleteChildRecord(Long recordId) {
		ChildRecordEntity childRecordEntity =
				childRecordRepository.findById(recordId)
						.orElseThrow(() ->
								new EntityNotFoundException("Record not found with ID: " + recordId)
						);

		childRecordRepository.delete(childRecordEntity);
	}

	public ChildRecordDTO findOneById(Long childRecordId) {
		ChildRecordEntity childRecord =
				childRecordRepository.findById(childRecordId).orElseThrow(() ->
					new EntityNotFoundException(String.format("ChildRecord 조회 실패: %d", childRecordId))
					);

		return ChildRecordDTO.builder()
				.id(childRecord.getId())
				.weight(childRecord.getWeight())
				.height(childRecord.getHeight())
				.registerDate(childRecord.getRegisterDate())
				.note(childRecord.getNote())
				.leftEye(childRecord.getLeftEye())
				.rightEye(childRecord.getRightEye())
				.childId(childRecord.getChild().getId())
				.build();
	}

	public HealthCheckEntity findHealthCheckOneById(Long healthCehckId) {
		return healthCheckRepository.findById(healthCehckId)
				.orElseThrow(() -> new EntityNotFoundException("healthCehckId를 찾지 못했습니다"));
	}

	public HealthCheckEntity FindHealthCehckByChildRecordId(Long childRecordId) {
		return healthCheckRepository.findByChildRecordId(childRecordId)
				.orElseThrow(() -> new EntityNotFoundException("Health Check를 찾지 못했습니다."));
	}

	/**
	 * 아이 신체 정보를 저장하는 메서드
	 */
	@Transactional
	public Long saveChildRecord(Long childId, Long memberId, ChildRecordInsertDTO childRecordInsertDTO) {

		// 아이 정보 조회
		ChildEntity child = childRepository.findById(childId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));

		// 멤버 (등록한 사람) 정보 조회
		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Member 조회 실패: %d", memberId)));

		// DTO 데이터를 Entity로 변환 후 저장
		ChildRecordEntity childRecord = ChildRecordEntity.builder()
				.child(child)
				.height(childRecordInsertDTO.height())
				.weight(childRecordInsertDTO.weight())
				.leftEye(childRecordInsertDTO.leftEye())
				.rightEye(childRecordInsertDTO.rightEye())
				.note(childRecordInsertDTO.note())
				.registerDate(childRecordInsertDTO.registerDate())
				.build();

		// 신체 정보 저장
		ChildRecordEntity savedChildRecord = childRecordRepository.save(childRecord);

        if (ObjectUtils.isEmpty(childRecordInsertDTO.healthCheckImg()) ||childRecordInsertDTO.healthCheckImg().isEmpty()) return savedChildRecord.getId();

        String savedFileName = FileManager
                .getSavedFileName(StringUtils.hasText(childRecordInsertDTO.healthCheckImg().getOriginalFilename())
                        ? childRecordInsertDTO.healthCheckImg().getOriginalFilename()
                        : Instant.now().toString().concat(".jpeg"));

        fileManager.saveFile(childRecordInsertDTO.healthCheckImg(), filePathUtil.childHealthCheckImgUploadPath(), savedFileName);

        HealthCheckEntity healthCheck = HealthCheckEntity.builder()
                .child(child)
                .member(member)
                .childRecord(childRecord)
                .savedFileName(savedFileName)
                .originalFileName(childRecordInsertDTO.healthCheckImg().getOriginalFilename())
                .build();

        healthCheckRepository.save(healthCheck);

		return savedChildRecord.getId();
	}

	/**
	 * Controller : handleGetChildRecordUpdateView
	 * API : v1.x.x-8
	 * C-7 이도훈
	 * @param childId
	 * @param childRecordId
	 * @return
	 */
	public ChildRecordUpdateViewDTO findOneByUpdateChildIdAndRecordId(Long childId, Long childRecordId) {
		ChildRecordEntity childRecord =
				childRecordRepository.findByChildIdAndRecordId(childId, childRecordId)
					.orElseThrow(() -> new EntityNotFoundException
							(String.format("아이 신체정보 조회실패: %d /%d", childId, childRecordId))
						);
		return ChildRecordUpdateViewDTO.builder()
				.id(childRecord.getId()) // record id
				.childId(childRecord.getChild().getId()) // childId 추가
				.registerDate(childRecord.getRegisterDate())
				.note(childRecord.getNote())
				.height(childRecord.getHeight())
				.weight(childRecord.getWeight())
				.leftEye(childRecord.getLeftEye())
				.rightEye(childRecord.getRightEye())
				.healthCheckImageSrc(childRecord.getHealthCheck().getSavedFileName())
				.build();
	}

	/**
	 * Controller : handlePostChildRecordUpdate
	 * API : v1.x.x-9
	 * C-7 이도훈
	 * @param childRecordId
	 * @param childRecordUpdateRequestDTO
	 */
	@Transactional
	public void updateChildRecord(Long childRecordId, ChildRecordUpdateRequestDTO childRecordUpdateRequestDTO)
	{

	    // 1. 기존 ChildRecordEntity 조회
	    ChildRecordEntity childRecord = childRecordRepository.findById(childRecordId)
	            .orElseThrow(() ->
	            	new EntityNotFoundException(String.format("ChildRecord 조회 실패: %d", childRecordId)
	            			));

	    // 2. 텍스트/숫자 필드 업데이트 (값이 있을 때만 변경)
	    childRecord.setNote(childRecordUpdateRequestDTO.note());
	    childRecord.setHeight(childRecordUpdateRequestDTO.height());
	    childRecord.setWeight(childRecordUpdateRequestDTO.weight());
	    childRecord.setLeftEye(childRecordUpdateRequestDTO.leftEye());
	    childRecord.setRightEye(childRecordUpdateRequestDTO.rightEye());
	    if (childRecordUpdateRequestDTO.registerDate() != null) {
	        childRecord.setRegisterDate(childRecordUpdateRequestDTO.registerDate());
	    }

	    // 3. 파일 관련 처리
		 // 새 파일이 없으면 -> 사용자가 기존 이미지를 삭제했을 가능성이 있으므로,
		 // 만약 기존 이미지가 있다면 삭제 처리 후 HealthCheckEntity의 파일 정보를 null로 변경합니다.
		 if (
			childRecordUpdateRequestDTO.healthCheckImg() ==
			null || childRecordUpdateRequestDTO.healthCheckImg().isEmpty()
			)	{
			 	if (childRecord.getHealthCheck() != null) {

			 		HealthCheckEntity healthCheck = childRecord.getHealthCheck();

			 		if (
			 			healthCheck.getSavedFileName() !=
			 			null && !healthCheck.getSavedFileName().isEmpty()
			 			)	{
			 			String existingFilePath = filePathUtil.childHealthCheckImgUploadPath()
			 					.concat(healthCheck.getSavedFileName());

			 			FileManager.deleteFile(existingFilePath);
			 			}
				         // 기존 HealthCheckEntity를 삭제하지 않고, 파일 관련 정보를 null로 변경합니다.
				         healthCheck.setOriginalFileName(null);
				         healthCheck.setSavedFileName(null);
			 		}
			 	return; // 파일 업데이트 없이 종료 (텍스트/숫자 정보만 업데이트)
		 }

	    // 4. 새 파일의 파일명 가져오기
	    String newFileName = childRecordUpdateRequestDTO.healthCheckImg().getOriginalFilename();

	    // 5. 파일 업데이트 처리
	    // 기존에 HealthCheckEntity가 존재하는 경우
	    if (childRecord.getHealthCheck() != null) {
	        HealthCheckEntity healthCheck = healthCheckRepository.findById(childRecord.getHealthCheck().getId())
	                .orElseThrow(() -> new EntityNotFoundException(
	                        String.format("HealthCheck 조회 실패: %d", childRecord.getHealthCheck().getId())
	                        ));

	        // 5-1. 기존 파일명과 새 파일명이 동일하면 파일 업데이트를 건너뜁니다.
	        if (Objects.equals(newFileName, healthCheck.getSavedFileName())) {
	            return;
	        }

	        // 5-2. 기존 파일 삭제 (savedFileName이 null이 아닐 경우에만)
	        if (healthCheck.getSavedFileName() !=null && !healthCheck.getSavedFileName().isEmpty()) {

	        	String existingFilePath =
	            		filePathUtil.childHealthCheckImgUploadPath().concat(healthCheck.getSavedFileName());

	            FileManager.deleteFile(existingFilePath);
	        }

	        // 5-3. 새 파일 저장 (실제 폴더에 저장)
	        String savedFileName = FileManager.getSavedFileName(newFileName);
	        fileManager.saveFile(
	        		childRecordUpdateRequestDTO.healthCheckImg(),
	                filePathUtil.childHealthCheckImgUploadPath(),
	                savedFileName
	        		);

	        // 5-4. DB의 HealthCheckEntity 업데이트
	        healthCheck.setOriginalFileName(newFileName);
	        healthCheck.setSavedFileName(savedFileName);
	    } else {
	        // 기존에 HealthCheckEntity가 없는 경우 → 새로 생성
	        String savedFileName = FileManager.getSavedFileName(newFileName);
	        fileManager.saveFile(childRecordUpdateRequestDTO.healthCheckImg(),
	                filePathUtil.childHealthCheckImgUploadPath(), savedFileName);

	        HealthCheckEntity newHealthCheck = HealthCheckEntity.builder()
	                .child(childRecord.getChild())
	                // member 정보가 필요하면 추가 (예: .member(member))
	                .childRecord(childRecord)
	                .originalFileName(newFileName)
	                .savedFileName(savedFileName)
	                .build();

	        healthCheckRepository.save(newHealthCheck);
	        childRecord.setHealthCheck(newHealthCheck);
	    }
	}
}
