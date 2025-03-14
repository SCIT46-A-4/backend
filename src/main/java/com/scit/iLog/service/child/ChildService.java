package com.scit.iLog.service.child;

import com.scit.iLog.controller.DashboardController;
import com.scit.iLog.domain.PermissionLevel;
import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.child.ChildBackGroundEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.child.FamilyBackGround;
import com.scit.iLog.domain.child.FamilyBackGroundEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.dto.child.*;
import com.scit.iLog.dto.dashboard.ParentDashboardChildListDTO;
import com.scit.iLog.exception.ChildNotFoundException;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.FamilyBackgroundRepository;
import com.scit.iLog.repository.MemberRepository;
import com.scit.iLog.repository.RelationShipRepository;
import com.scit.iLog.util.FileManager;
import com.scit.iLog.util.FilePathUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.scit.iLog.config.WebConfig.CHILD_PROFILE_REQUEST_ROOT_PATH;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildService {
    private final ChildRepository childRepository;
    private final RelationShipRepository relationShipRepository;
    private final FilePathUtil filePathUtil;
    private final FileManager fileManager;
    private final FamilyBackgroundRepository familyBackgroundRepository;
    private final MemberRepository memberRepository;

    // 25/2/12 준: api-23 아이 새로운 아이 등록(저장)
    @Transactional
    public Long saveBasicInfo(Long memberId, ChildBasicInfoInsertDTO childBasicInfoInsertDTO) {
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

        MemberEntity member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Member 조회 실패: %d", memberId)));
        RelationShipEntity relationShip = RelationShipEntity.builder()
                .relationType(member.getRelationType())
                .child(child)
                .member(member)
                .permissionLevel(PermissionLevel.OWNER)
                .build();
        relationShipRepository.save(relationShip);
        return child.getId();
    }

    // 25/2/13 준: api-??: 아이 정보 찾아서 반환
    public ChildRecordListBasicInfoDTO findBasicInfoById(Long childId) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));
        return ChildRecordListBasicInfoDTO.builder()
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

    @Transactional(readOnly = true)
    public ChildBasicInfoDetailsDTO getBasicInfoById(Long childId) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));

        // 2025-02-28 / 김은진 / 가정환경 정보 조회
        // 가정환경 정보 조회
        List<FamilyBackGround> familyBackGrounds = child.getChildBackGrounds().stream()
                .map(childBackground -> childBackground.getFamilyBackGround().getFamilyBackGround())
                .collect(Collectors.toList());

        return ChildBasicInfoDetailsDTO.builder()
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

    public String getChildNameById(Long childId) {
        return childRepository
                .findById(childId)
                .orElseThrow(() -> new ChildNotFoundException(childId))
                .getName();
    }

    // child의 정보를 수정하는 함수
    @Transactional
    public void updateChildBasicInfo(Long childId, ChildBasicInfoUpdateDTO childBasicInfoUpdateDTO) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));

        // 기본 정보 업데이트
        child.setName(childBasicInfoUpdateDTO.name());
        child.setGender(childBasicInfoUpdateDTO.gender());
        child.setBirthDate(childBasicInfoUpdateDTO.birthDate().atStartOfDay());
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
            String existingFilePath = filePathUtil.childProfileImgUploadPath().concat("/").concat(child.getSavedProfileImgName());
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
                        .childId(relationShip.getChild().getId())
                        .birthDate(relationShip.getChild().getBirthDate())
                        .birthLocation(relationShip.getChild().getBirthLocation())
                        .gender(relationShip.getChild().getGender().getTypeNameKr())
                        .callName(relationShip.getChild().getCallName())
                        .name(relationShip.getChild().getName())
                        .profileImgSrc(
                                CHILD_PROFILE_REQUEST_ROOT_PATH.concat(
                                        StringUtils.hasText(relationShip.getChild().getSavedProfileImgName()) ?
                                                relationShip.getChild().getSavedProfileImgName() :
                                                ChildEntity.DEFAULT_PROFILE_IMG
                                ))
                        .build())
                .toList();
        return new ParentDashboardChildListDTO(childProfiles.size(), childProfiles);
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


    //-------------------------------------------------------------------------------------
    /*
     * v1.x.x-11
     * D-2
     * handleGetTeacherDashboardView
     * 2025-03-04 / 김은진 / 교사용 대시보드에서 모든 아이들의 기본 정보 조회
     */
    @Transactional(readOnly = true)
    public List<ChildBasicInfoDTO> getAllChildrenBasicInfo(DashboardController.SortOption sortOption) {
        List<ChildEntity> children;
        switch (sortOption) {
            case NAME:
                children = childRepository.findAllByOrderByNameAsc();
                break;
            case BIRTH_DATE:
                children = childRepository.findAllByOrderByBirthDateAsc();
                break;
            case REGISTER_DATE:
                children = childRepository.findAllByOrderByCreatedAtDesc();
                break;
            default:
                children = childRepository.findAll();
        }

        return children.stream()
                .map(child -> ChildBasicInfoDTO.builder()
                        .id(child.getId())
                        .name(child.getName())
                        .birthDate(child.getBirthDate())
                        .gender(child.getGender())
                        .profileImgSrcUri(filePathUtil.childProfileImgUploadPath())
                        .note(child.getNote())
                        .build())
                .collect(Collectors.toList());
    }

    /*
     * v1.x.x-12
     * D-2
     * getChildProfile
     * 2025-03-04 / 김은진 / 특정 아이의 기본 정보 조회
     */
    @Transactional(readOnly = true)
    public ChildBasicInfoDTO getChildBasicInfo(Long childId) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException("Child not found with id: " + childId));

        // 프로필 이미지 경로 설정
        String profileImgPath;
        if (child.getSavedProfileImgName() != null
                && !child.getSavedProfileImgName().equals(ChildEntity.DEFAULT_PROFILE_IMG)) {
            profileImgPath = "/childProfile/" + child.getSavedProfileImgName();
        } else {
            profileImgPath = "/static/images/default-profile.png"; // static 폴더 경로 추가
        }

        return ChildBasicInfoDTO.builder()
                .id(child.getId())
                .name(child.getName())
                .birthDate(child.getBirthDate())
                .gender(child.getGender())
                .profileImgSrcUri(profileImgPath)
                .familyBackGrounds(child.getChildBackGrounds().stream()
                        .map(bg -> bg.getFamilyBackGround().getFamilyBackGround())
                        .collect(Collectors.toList()))
                .note(child.getNote())
                .build();
    }
    //김은진 끝 -----------------------------------------------------------------

    //2025-03-11 이도훈 작성
    public ChildEntity findById(Long childId) {
        return childRepository.findById(childId)
                .orElseThrow(() -> new ChildNotFoundException(childId));
    }
}