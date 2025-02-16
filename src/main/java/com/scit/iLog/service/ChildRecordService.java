package com.scit.iLog.service;

import com.scit.iLog.domain.child.ChildRecordEntity;
import com.scit.iLog.dto.child.ChildRecordDTO;
import com.scit.iLog.repository.ChildRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.scit.iLog.config.WebConfig.HEALTHCHECK_IMAGES_REQUEST_PATH;
import static com.scit.iLog.config.WebConfig.HEALTHCHECK_IMAGES_REQUEST_ROOT_PATH;

@Service
@RequiredArgsConstructor
public class ChildRecordService {
    private final ChildRecordRepository childRecordRepository;

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
                .healthCheckImageSrc(HEALTHCHECK_IMAGES_REQUEST_ROOT_PATH.concat(childRecord.getSavedPhotoName()))
                .build();
    }
}
