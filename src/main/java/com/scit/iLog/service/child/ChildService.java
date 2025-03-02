package com.scit.iLog.service.child;

import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.child.ChildBackGroundEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.child.FamilyBackGround;
import com.scit.iLog.domain.child.FamilyBackGroundEntity;
import com.scit.iLog.dto.dashboard.ParentDashboardChildListDTO;
import com.scit.iLog.dto.child.ChildBasicInfoDTO;
import com.scit.iLog.dto.child.ChildBasicInfoInsertDTO;
import com.scit.iLog.dto.child.ChildBasicInfoUpdateDTO;
import com.scit.iLog.dto.child.ChildProfileDTO;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.FamilyBackgroundRepository;
import com.scit.iLog.repository.RelationShipRepository;
import com.scit.iLog.util.FileManager;
import com.scit.iLog.util.FilePathUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.scit.iLog.config.WebConfig.CHILD_PROFILE_REQUEST_ROOT_PATH;
import static org.springframework.util.StringUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildService {
    private final ChildRepository childRepository;
    private final RelationShipRepository relationShipRepository;
    private final FilePathUtil filePathUtil;
    private final FileManager fileManager;
    private final FamilyBackgroundRepository familyBackgroundRepository;

    // 25/2/12 준: api-23 아이 새로운 아이 등록(저장)
    @Transactional
    public Long saveBasicInfo(ChildBasicInfoInsertDTO childBasicInfoInsertDTO) {
        log.info("gender: {}", childBasicInfoInsertDTO.gender());
        ChildEntity child = childRepository.save(ChildEntity.builder()
                .name(childBasicInfoInsertDTO.name())
                .gender(childBasicInfoInsertDTO.gender())
                .birthDate(LocalDateTime.of(childBasicInfoInsertDTO.birthDate(), LocalTime.of(0, 0, 0)))
                .birthLocation(childBasicInfoInsertDTO.birthLocation())
                .callName(childBasicInfoInsertDTO.callName())
                .note(childBasicInfoInsertDTO.note())
                .build());

        // 2025-02-28 / 김은진 수정/ 프로필 이미지 처리
        MultipartFile profileImg = childBasicInfoInsertDTO.profileImg();
        if (!ObjectUtils.isEmpty(profileImg) && !profileImg.isEmpty()) {
            try {
                String savedFileName = FileManager.getSavedFileName(
                        hasText(profileImg.getOriginalFilename())
                                ? Normalizer.normalize(profileImg.getOriginalFilename(), Normalizer.Form.NFC)
                                //@TODO 멤버 아이디나 이름을 사용해서 사용자에게 친근한 이름으로 바꾸기
                                : "unknown"
                                .concat(Instant.now().toString())
                                .concat(".jpeg"));

                fileManager.saveFile(profileImg, filePathUtil.childProfileImgUploadPath(), savedFileName);
                child.setOriginalProfileImgName(profileImg.getOriginalFilename());
                child.setSavedProfileImgName(savedFileName);
            } catch (Exception e) {
                log.error("프로필 이미지 저장 처리 중 오류 발생", e);
                // 이미지 처리 실패 시 기본 이미지 유지
                child.setDefaultProfileImg();
            }
        }

        // 25/2/12 준성: child 저장 후 저장된 id값 반환
        // 2025-02-28 / 김은진 수정 / 가정환경 데이터 저장
        // 가정환경 데이터 저장
        if (!ObjectUtils.isEmpty(childBasicInfoInsertDTO.familyBackGrounds())
                && !childBasicInfoInsertDTO.familyBackGrounds().isEmpty()
        ) {
            List<ChildBackGroundEntity> childBackGrounds = familyBackgroundRepository.findAll()
                    .stream()
                    .filter(familyBackGround ->
                            childBasicInfoInsertDTO.familyBackGrounds().contains(familyBackGround.getFamilyBackGround()))
                    .map(familyBackGround -> ChildBackGroundEntity.builder()
                            .child(child)
                            .familyBackGround(familyBackGround)
                            .build())
                    .toList();
            child.replaceAllChildBackGrounds(childBackGrounds);
        }
        return child.getId();
    }

    // 25/2/13 준: api-??: 아이 정보 찾아서 반환
    public ChildBasicInfoDTO findById(Long childId) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));
        return ChildBasicInfoDTO.builder()
                .id(child.getId())
                .name(child.getName())
                .birthDate(child.getBirthDate())
                .gender(child.getGender())
                .birthLocation(child.getBirthLocation())
                .profileImgSrcUri(CHILD_PROFILE_REQUEST_ROOT_PATH + child.getSavedProfileImgName())
                .callName(child.getCallName())
                .note(child.getNote())
                .build();
    }

    // child의 정보를 수정하는 함수
    @Transactional
    public void updateChildBasicInfo(Long childId, ChildBasicInfoUpdateDTO childBasicInfoUpdateDTO) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));

        // 기본 정보 업데이트
        child.setName(childBasicInfoUpdateDTO.name());
        child.setGender(childBasicInfoUpdateDTO.gender());
        child.setBirthDate(childBasicInfoUpdateDTO.birthDate());
        child.setBirthLocation(childBasicInfoUpdateDTO.birthLocation());
        child.setNote(childBasicInfoUpdateDTO.note());
        child.setCallName(childBasicInfoUpdateDTO.callName()); // callName 업데이트 추가 (2025-02-28 / 김은진)

        // 2025-02-28 / 김은진 / 가정환경 데이터 업데이트
        // 가정환경 정보 업데이트
        if (!ObjectUtils.isEmpty(childBasicInfoUpdateDTO.familyBackGrounds())
                && !childBasicInfoUpdateDTO.familyBackGrounds().isEmpty()) {
            List<ChildBackGroundEntity> childBackGrounds = familyBackgroundRepository.findAll().stream()
                    .filter(familyBackGround ->
                            childBasicInfoUpdateDTO.
                                    familyBackGrounds().
                                    contains(familyBackGround.getFamilyBackGround()))
                    .map(familyBackGround ->
                            ChildBackGroundEntity.builder()
                            .child(child)
                            .familyBackGround(familyBackGround)
                            .build())
                    .toList();
            child.replaceAllChildBackGrounds(childBackGrounds);
        } else {
            child.deleteAllChildBackground();
        }

        // 2025-02-28 / 김은진 / 프로필 이미지 처리 - 새 이미지가 업로드된 경우에만 처리
        MultipartFile profileImg = childBasicInfoUpdateDTO.profileImg();
        // 이미지가 삭제된 경우 (빈 파일이 전송된 경우)
        if (ObjectUtils.isEmpty(profileImg)) {
            String existingFilePath = filePathUtil.childProfileImgUploadPath().concat(ChildEntity.DEFAULT_PROFILE_IMG);
            FileManager.deleteFile(existingFilePath);
            child.setDefaultProfileImg();
            return;
        }
        // 새 이미지가 없으면 여기서 종료
        if (profileImg.isEmpty()) return;
        // 그렇지 않으면 기존 이미지 삭제 후 새 이미지 저장
        if (hasText(child.getOriginalProfileImgName())) {
            String existingFilePath = filePathUtil.childProfileImgUploadPath().concat(child.getSavedProfileImgName());
            FileManager.deleteFile(existingFilePath);
        }
        String originalFileName = hasText(profileImg.getOriginalFilename())
                ? Normalizer.normalize(profileImg.getOriginalFilename(), Normalizer.Form.NFC)
                : Instant.now().toString().concat(".jpg");
        String savedFileName = FileManager.getSavedFileName(originalFileName);
        // 새 이미지 저장
        fileManager.saveFile(profileImg, filePathUtil.childProfileImgUploadPath(), savedFileName);
        child.setSavedProfileImgName(savedFileName);
        child.setOriginalProfileImgName(Normalizer.normalize(profileImg.getOriginalFilename(), Normalizer.Form.NFC));
    }

    /**
     * 아동 정보를 삭제하는 메소드
     * 아동의 프로필 이미지도 함께 삭제
     *
     * @param childId 삭제할 아동의 ID
     * @throws EntityNotFoundException 해당 ID의 아동이 존재하지 않는 경우
     */
    @Transactional
    public void deleteChildById(Long childId) {
        // 삭제할 아동 정보 조회
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));

        // 프로필 이미지 삭제 (기본 이미지가 아닌 경우에만)
        if (!child.getSavedProfileImgName().equals(ChildEntity.DEFAULT_PROFILE_IMG)) {
                // 저장된 이미지 파일의 전체 경로 생성
                String existingFilePath = filePathUtil.childProfileImgUploadPath().concat(child.getSavedProfileImgName());
                FileManager.deleteFile(existingFilePath);
        }
        childRepository.deleteById(childId);
    }

    @Transactional(readOnly = true)
    public ParentDashboardChildListDTO getChildrenProfilesOf(Long memberId) {
        List<RelationShipEntity> relationShips = relationShipRepository.findAllByMemberId(memberId);
        List<ChildProfileDTO> childProfiles = relationShips.stream()
                .map(relationShip -> ChildProfileDTO.builder()
                        .id(relationShip.getChild().getId())
                        .birthDate(relationShip.getChild().getBirthDate())
                        .name(relationShip.getChild().getName())
                        .profileImgSrc(
                                // 파일 이름은 확장자를 포함
                                CHILD_PROFILE_REQUEST_ROOT_PATH.concat(
                                        relationShip.getChild().getSavedProfileImgName()))
                        .build())
                .toList();
        return new ParentDashboardChildListDTO(
                childProfiles.size(),
                childProfiles);
    }

    @Transactional(readOnly = true)
    public ChildBasicInfoDTO getBasicInfoById(Long childId) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));

        // 2025-02-28 / 김은진 / 가정환경 정보 조회
        // 가정환경 정보 조회
        List<FamilyBackGround> familyBackGrounds = child.getChildBackGrounds().stream()
                .map(childBackground -> childBackground.getFamilyBackGround().getFamilyBackGround())
                .collect(Collectors.toList());

        return ChildBasicInfoDTO.builder()
                .id(child.getId())
                .name(child.getName())
                .birthDate(child.getBirthDate())
                .note(child.getNote())
                .profileImgSrcUri(hasText(child.getOriginalProfileImgName())
                        ? CHILD_PROFILE_REQUEST_ROOT_PATH.concat(child.getSavedProfileImgName())
                        : CHILD_PROFILE_REQUEST_ROOT_PATH.concat(ChildEntity.DEFAULT_PROFILE_IMG)) // 기본 이미지
                .gender(child.getGender())
                .callName(child.getCallName())
                .birthLocation(child.getBirthLocation())
                .familyBackGrounds(familyBackGrounds) // 가정환경 정보 설정(2025-02-28 / 김은진)
                .build();
    }

    /*
     * 2024-02-27 김보경
     * getFamilyBackgrounds 정보 담기?
     */
    @Transactional(readOnly = true)
    public List<FamilyBackGround> getFamilyBackgrounds(Long childId) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));

        // 기존의 가정환경 데이터를 리스트로 변환하여 반환
        return child.getChildBackGrounds().stream()
                .map(ChildBackGroundEntity::getFamilyBackGround)
                .map(FamilyBackGroundEntity::getFamilyBackGround)
                .toList();
    }
}