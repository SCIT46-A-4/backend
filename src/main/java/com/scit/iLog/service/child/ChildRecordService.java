package com.scit.iLog.service.child;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.child.ChildRecordEntity;
import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.dto.child.*;
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

    public ChildRecordDTO findOneById(Long childRecordId) {
        ChildRecordEntity childRecord = childRecordRepository.findById(childRecordId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("ChildRecord 조회 실패: %d", childRecordId)));
        return ChildRecordDTO.builder()
                .id(childRecord.getId())
                .weight(childRecord.getWeight())
                .height(childRecord.getHeight())
                .registerDate(childRecord.getRegisterDate())
                .note(childRecord.getNote())
                .leftEye(childRecord.getLeftEye())
                .rightEye(childRecord.getRightEye())
                .build();
    }

    @Transactional
    public Long saveChildRecord(Long childId, Long memberId, ChildRecordInsertDTO childRecordInsertDTO) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));
        ChildRecordEntity childRecord = ChildRecordEntity.builder()
                .child(child)
                .note(childRecordInsertDTO.note())
                .height(childRecordInsertDTO.height())
                .weight(childRecordInsertDTO.weight())
                .leftEye(childRecordInsertDTO.leftEye())
                .rightEye(childRecordInsertDTO.rightEye())
                .build();
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Member 조회 실패: %d", memberId)));
        ChildRecordEntity savedChildRecord = childRecordRepository.save(childRecord);
        if (childRecordInsertDTO.healthCheckImg().isEmpty()) return savedChildRecord.getId();

        fileManager.saveFile(childRecordInsertDTO.healthCheckImg(),filePathUtil.childHealthCheckImgUploadPath());

        HealthCheckEntity healthCheck = HealthCheckEntity.builder()
                .child(child)
                .member(member)
                .savedFileName(
                        FileManager
                                .getSavedFileName(
                                        ObjectUtils.isEmpty(childRecordInsertDTO.healthCheckImg().getOriginalFilename()) ?
                                                Instant.now().toString() :
                                                childRecordInsertDTO.healthCheckImg().getOriginalFilename()
                                ))
                .originalFileName(
                        childRecordInsertDTO
                                .healthCheckImg().getOriginalFilename()
                )
                .build();

        healthCheckRepository.save(healthCheck);

        return savedChildRecord.getId();
    }

    @Transactional
    public void updateChildRecord(Long childRecordId, ChildRecordUpdateRequestDTO childRecordUpdateRequestDTO) {
        ChildRecordEntity childRecord = childRecordRepository.findById(childRecordId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("ChildRecord 조회 실패: %d", childRecordId)));
        childRecord.setNote(childRecordUpdateRequestDTO.note());
        childRecord.setHeight(childRecordUpdateRequestDTO.height());
        childRecord.setWeight(childRecord.getWeight());
        childRecord.setLeftEye(childRecord.getLeftEye());
        childRecord.setRegisterDate(childRecordUpdateRequestDTO.registerDate());

        if (childRecordUpdateRequestDTO.healthCheckImg().isEmpty()) return;

        String existingFilePath = filePathUtil
                .childHealthCheckImgUploadPath()
                .concat(childRecord.getHealthCheck().getSavedFileName());
        FileManager.deleteFile(existingFilePath);
        fileManager.saveFile(
                childRecordUpdateRequestDTO.healthCheckImg(),
                filePathUtil.childHealthCheckImgUploadPath());


        HealthCheckEntity healthCheck = healthCheckRepository.findById(childRecord.getHealthCheck().getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                String.format("HealthCheck 조회 실패: %d", childRecord.getHealthCheck().getId())));
        healthCheck.setOriginalFileName(childRecordUpdateRequestDTO.healthCheckImg().getOriginalFilename());
        healthCheck.setSavedFileName(
                FileManager
                        .getSavedFileName(
                                ObjectUtils.isEmpty(childRecordUpdateRequestDTO.healthCheckImg().getOriginalFilename()) ?
                                        Instant.now().toString() :
                                        childRecordUpdateRequestDTO.healthCheckImg().getOriginalFilename()
                        )
        );
    }
}
