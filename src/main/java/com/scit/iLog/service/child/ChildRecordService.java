package com.scit.iLog.service.child;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.child.ChildRecordEntity;
import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.dto.child.*;
import com.scit.iLog.exception.MemberNotFoundException;
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
	 * @param childRecordId
	 */
	@Transactional
	public void deleteChildRecord(Long childRecordId) {
		ChildRecordEntity childRecordEntity = findChildRecordById(childRecordId);
		childRecordRepository.delete(childRecordEntity);
	}

	@Transactional(readOnly = true)
	public ChildRecordDetailsDTO findDetailsById(Long childRecordId) {
		ChildRecordEntity childRecord = findChildRecordById(childRecordId);
		return ChildRecordDetailsDTO.builder()
				.childRecordId(childRecord.getId())
				.weight(childRecord.getWeight())
				.height(childRecord.getHeight())
				.registerDate(childRecord.getRegisterDate())
				.note(childRecord.getNote())
				.leftEye(childRecord.getLeftEye())
				.rightEye(childRecord.getRightEye())
				.healthCheckImage(
						new HealthCheckImageDTO(
								!ObjectUtils.isEmpty(childRecord.getHealthCheck()),
								ObjectUtils.isEmpty(childRecord.getHealthCheck()) ? null :
										HEALTHCHECK_IMAGES_REQUEST_ROOT_PATH.concat(childRecord.getHealthCheck().getSavedFileName())))
				.build();
	}

	public String findHealthCheckFileNameByChildRecordId(Long childRecordId) {
		return healthCheckRepository.findByChildRecordId(childRecordId)
				.orElseThrow(() -> new EntityNotFoundException("Health Check를 찾지 못했습니다."))
				.getSavedFileName();
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
	 * @param childRecordId
	 * @return
	 */
	public ChildRecordUpdateViewDTO getChildRecordUpdateInfoByRecordId(Long childRecordId) {
		ChildRecordEntity childRecord = findChildRecordById(childRecordId);
		return ChildRecordUpdateViewDTO.builder()
				.id(childRecord.getId()) // record id
				.childId(childRecord.getChild().getId()) // childId 추가
				.registerDate(childRecord.getRegisterDate())
				.note(childRecord.getNote())
				.height(childRecord.getHeight())
				.weight(childRecord.getWeight())
				.leftEye(childRecord.getLeftEye())
				.rightEye(childRecord.getRightEye())
				.healthCheckImage(
						new HealthCheckImageDTO(
								!ObjectUtils.isEmpty(childRecord.getHealthCheck()),
								ObjectUtils.isEmpty(childRecord.getHealthCheck()) ? null :
										HEALTHCHECK_IMAGES_REQUEST_ROOT_PATH.concat(childRecord.getHealthCheck().getSavedFileName())))
				.build();
	}

	/**
	 * Controller : handlePostChildRecordUpdate
	 * API : v1.x.x-9
	 * C-7 이도훈
	 * @param childRecordId
	 */
	@Transactional
	public void updateChildRecord(Long childId, Long memberId, Long childRecordId, ChildRecordUpdateRequestDTO childRecordUpdateRequestDTO) {
		ChildRecordEntity childRecord = findChildRecordById(childRecordId);
		ChildEntity child = childRepository.findById(childId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d",childId)));
		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Member 조회 실패: %d", memberId)));
		// 1. 기본 필드 업데이트
		updateBasicFields(childRecord, childRecordUpdateRequestDTO);

		// 2. 파일 처리: 새 파일 요청이 있으면 업데이트, 그렇지 않으면 기존 파일 삭제 처리
		if (isFileUpdateRequested(childRecordUpdateRequestDTO)) {
			processHealthCheckFileUpdate(child, member, childRecord, childRecordUpdateRequestDTO);
		} else {
			processHealthCheckFileDeletion(childRecord);
		}
	}

	private ChildRecordEntity findChildRecordById(Long childRecordId) {
		return childRecordRepository.findById(childRecordId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("ChildRecord 조회 실패: %d", childRecordId)));
	}

	private void updateBasicFields(ChildRecordEntity childRecord, ChildRecordUpdateRequestDTO childRecordUpdateRequestDTO) {
		childRecord.setNote(childRecordUpdateRequestDTO.note());
		childRecord.setHeight(childRecordUpdateRequestDTO.height());
		childRecord.setWeight(childRecordUpdateRequestDTO.weight());
		childRecord.setLeftEye(childRecordUpdateRequestDTO.leftEye());
		childRecord.setRightEye(childRecordUpdateRequestDTO.rightEye());
		if (ObjectUtils.isEmpty(childRecordUpdateRequestDTO.registerDate())) return;
		childRecord.setRegisterDate(childRecordUpdateRequestDTO.registerDate());
	}

	private boolean isFileUpdateRequested(ChildRecordUpdateRequestDTO request) {
		return !ObjectUtils.isEmpty(request.healthCheckImg()) && !request.healthCheckImg().isEmpty();
	}

	private void processHealthCheckFileDeletion(ChildRecordEntity childRecord) {
		HealthCheckEntity healthCheck = childRecord.getHealthCheck();
		if (ObjectUtils.isEmpty(healthCheck)) return;
		String existingFileName = healthCheck.getSavedFileName();
		if (existingFileName != null && !existingFileName.isEmpty()) {
			String existingFilePath = filePathUtil.childHealthCheckImgUploadPath().concat(existingFileName);
			FileManager.deleteFile(existingFilePath);
		}
		// 파일 관련 정보를 null 처리하여 삭제 효과 반영
		healthCheck.setOriginalFileName(null);
		healthCheck.setSavedFileName(null);
	}

	private void processHealthCheckFileUpdate(ChildEntity child, MemberEntity member, ChildRecordEntity childRecord, ChildRecordUpdateRequestDTO request) {
		String newOriginalFileName = StringUtils.hasText(request.healthCheckImg().getOriginalFilename()) ?
				request.healthCheckImg().getOriginalFilename() : "unknown".concat(Instant.now().toString()).concat(".jpg");

		if (!ObjectUtils.isEmpty(childRecord.getHealthCheck())) {
			// 기존 HealthCheckEntity가 존재하는 경우 DB에서 재조회 후 업데이트
			HealthCheckEntity existingHealthCheck = childRecord.getHealthCheck();

			// 기존 파일 삭제
			String existingFileName = existingHealthCheck.getSavedFileName();
			if (StringUtils.hasText(existingFileName) && !existingFileName.isEmpty()) {
				String existingFilePath = filePathUtil.childHealthCheckImgUploadPath().concat(existingFileName);
				FileManager.deleteFile(existingFilePath);
			}

			healthCheckRepository.deleteById(existingHealthCheck.getId());
		}
		// 새 파일 저장 및 HealthCheckEntity 업데이트
		String newSavedFileName = FileManager.getSavedFileName(newOriginalFileName);
		fileManager.saveFile(request.healthCheckImg(), filePathUtil.childHealthCheckImgUploadPath(), newSavedFileName);

		HealthCheckEntity healthCheck = healthCheckRepository.save(HealthCheckEntity.builder()
				.childRecord(childRecord)
				.child(child)
				.member(member)
				.originalFileName(newOriginalFileName)
				.savedFileName(newSavedFileName)
				.build());
		childRecord.setHealthCheck(healthCheck);
	}

	@Transactional
	public void inValidateByMember(Long memberId) {
		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberNotFoundException(memberId));
		healthCheckRepository.findAllByMember(member)
				.forEach(healthCheck -> healthCheck.setMember(null));
	}
}
