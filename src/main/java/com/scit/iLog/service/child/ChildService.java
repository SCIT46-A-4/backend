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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.scit.iLog.config.WebConfig.CHILD_PROFILE_REQUEST_ROOT_PATH;

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

			ChildEntity child = ChildEntity.builder()
					.name(childBasicInfoInsertDTO.name())
					.gender(childBasicInfoInsertDTO.gender())
					.birthDate(childBasicInfoInsertDTO.birthDate())
					.callName(childBasicInfoInsertDTO.callName())
					.note(childBasicInfoInsertDTO.note())
					.build();
			if (!childBasicInfoInsertDTO.profileImg().isEmpty()) {
				fileManager.saveFile(childBasicInfoInsertDTO.profileImg(), filePathUtil.childProfileImgUploadPath());
				child.setOriginalProfileImgName(childBasicInfoInsertDTO.profileImg().getOriginalFilename());
				child.setSavedProfileImgName(FileManager.getSavedFileName(
						ObjectUtils.isEmpty(childBasicInfoInsertDTO.profileImg().getOriginalFilename()) ?
								Instant.now().toString().concat(".jpeg") :
								childBasicInfoInsertDTO.profileImg().getOriginalFilename()
				));
			}

			// 25/2/12 준성: child 저장 후 저장된 id값 반환

			Map<FamilyBackGround, FamilyBackGroundEntity> familyBackgroundMap = familyBackgroundRepository
					.findAll()
					.stream()
					.collect(
							Collectors
									.toMap(
											FamilyBackGroundEntity::getFamilyBackGround,
											familyBackGround -> familyBackGround));

			List<ChildBackGroundEntity> childBackGrounds = childBasicInfoInsertDTO.familyBackGrounds()
						.stream()
						.map(familyBackGround ->
								ChildBackGroundEntity.builder()
										.child(child)
										.familyBackGround(familyBackgroundMap.get(familyBackGround))
										.build()
						)
						.toList();

			ChildEntity _child = childRepository.save(
					ChildEntity.builder()
							.name(childBasicInfoInsertDTO.name())
							.birthDate(childBasicInfoInsertDTO.birthDate())
							.gender(childBasicInfoInsertDTO.gender())
							.note(childBasicInfoInsertDTO.note())
							.birthLocation(childBasicInfoInsertDTO.birthLocation())
							.callName(childBasicInfoInsertDTO.callName())
							.build()
			);
			return _child.getId();
		}

		//25/2/13 준: api-??: 아이 정보 찾아서 반환
		public ChildBasicInfoDTO findById(Long childId)
			{
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
		public void updateChildBasicInfo(Long childId, ChildBasicInfoUpdateDTO childBasicInfoUpdateDTO)
			{
				ChildEntity child = childRepository.findById(childId)
						.orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));
				child.setName(childBasicInfoUpdateDTO.name());
				child.setGender(childBasicInfoUpdateDTO.gender());
				child.setBirthDate(childBasicInfoUpdateDTO.birthDate());
				child.setBirthLocation(childBasicInfoUpdateDTO.birthLocation());
				child.setNote(childBasicInfoUpdateDTO.note());

				if (childBasicInfoUpdateDTO.profileImg().isEmpty()) return;

				String existingFilePath = filePathUtil
						.childProfileImgUploadPath()
						.concat(child.getSavedProfileImgName());
				FileManager.deleteFile(existingFilePath);
				fileManager.saveFile(
						childBasicInfoUpdateDTO.profileImg(),
						filePathUtil.childProfileImgUploadPath());
				//@TODO 파일명 바꿔줘야지
			}

		// 아이 정보 삭제하는 메소드
		@Transactional
		public void deleteChildById(Long childId) {
			childRepository.deleteById(childId);
		}

		@Transactional(readOnly = true)
		public ParentDashboardChildListDTO getChildrenProfilesOf(Long memberId) {
			List<RelationShipEntity> relationShips = relationShipRepository.findAllByMemberId(memberId);
			List<ChildProfileDTO> childProfiles = relationShips.stream()
					.map(relationShip ->
							ChildProfileDTO.builder()
									.id(relationShip.getChild().getId())
									.birthDate(relationShip.getChild().getBirthDate())
									.name(relationShip.getChild().getName())
									.profileImgSrc(
													//파일 이름은 확장자를 포함하고있습니다.
													CHILD_PROFILE_REQUEST_ROOT_PATH.concat(
													relationShip.getChild().getSavedProfileImgName())
									)
									.build()
					)
					.toList();
			return new ParentDashboardChildListDTO(
					childProfiles.size(),
					childProfiles
			);
		}

		@Transactional(readOnly = true)
		public ChildBasicInfoDTO selectBasicInfoById(Long childId) {
			ChildEntity child = childRepository.findById(childId)
					.orElseThrow(() -> new EntityNotFoundException(String.format("Child 조회 실패: %d", childId)));
			return ChildBasicInfoDTO.builder()
						.id(child.getId())
						.name(child.getName())
						.birthDate(child.getBirthDate())
						.note(child.getNote())
						.profileImgSrcUri(CHILD_PROFILE_REQUEST_ROOT_PATH.concat(child.getSavedProfileImgName()))
						.gender(child.getGender())
						.callName(child.getCallName())
						.birthLocation(child.getBirthLocation())
						.build();
		}
	}
