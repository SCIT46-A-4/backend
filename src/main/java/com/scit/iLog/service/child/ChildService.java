package com.scit.iLog.service.child;
import java.util.List;
import java.util.Optional;

import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.dto.child.ChildProfileDTO;
import com.scit.iLog.dto.ParentDashboardChildListDTO;
import com.scit.iLog.repository.*;
import com.scit.iLog.util.FilePathUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.dto.child.ChildBasicInfoDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildService
	{
		private final ChildRepository childRepository;
		private final RelationShipRepository relationShipRepository;
		private final FilePathUtil filePathUtil;

		// 25/2/12 준: api-23 아이 새로운 아이 등록(저장)
		@Transactional
		public Long saveBasicInfo(ChildBasicInfoDTO childBasicInfoDto)
			{
				// 25/2/12 ㅈ: child 저장 후 저장된 id값 반환
				/*
					@TODO
					안좋은 변수명!!!
				 */
				ChildEntity entity = ChildEntity.builder()
									.gender(childBasicInfoDto.getGender())
									.name(childBasicInfoDto.getName())
									.birthDate(childBasicInfoDto.getBirthDate())
									.build();

				ChildEntity _child = childRepository.save(entity);

				return _child.getId();
			}

		//25/2/13 준: api-??: 아이 정보 찾아서 반환
		/*
			@TODO 안좋은 파라미터명!!!
			이렇게 하면 무슨 아이디가 넘어오는지 명확하지 않습니다.
			그리고 Exception이라는 최상위 부모 클래스 에러를 던지는건 안하느니만 못합니다...
		 */
		public ChildBasicInfoDTO findById(Long childId) throws Exception
			{
				// _entity 받아오는 함수
				/*
					@TODO
					너무 안좋은 변수명!!!
					그리고 이렇게 할바에는 조회 실패시 orElseThrow로 에러 던지는게 좋습니다.
					그리고 _이거는 보통 private field에 사용하는 네이밍 컨벤션인데 여기는
					그냥 지역변수를 선언하는 것이기에 상황에 맞지 않습니다...
				 */
				Optional<ChildEntity> _entity = childRepository.findById(childId);
				if(!_entity.isPresent())
					{
						throw new Exception("파일을 찾을 수 없습니다");
					}

				// 아이 정보 체크 받아오기(함수에 넣으면 entity -> childDTO로 변경)
				return ChildBasicInfoDTO.builder()
						.id(_entity.get().getId())
						.name(_entity.get().getName())
						.birthDate(_entity.get().getBirthDate())
						.gender(_entity.get().getGender())
						.build();
			}


		// child의 정보를 수정하는 함수
		@Transactional
		public void updateChildData(Long childId, ChildBasicInfoDTO childBasicInfoDto)
			{
				/*
				 	@TODO
				 	이렇게하면 안됩니다!!!
				 	이렇게하면 기존의 엔티티가 갖고있는 값을 싹 다 덮어씌워버립니다;;
				 	무조건 세터로 값을 수정해야합니다.
				 */
				ChildEntity _entity = ChildEntity.builder()
									.id(childId)
									.name(childBasicInfoDto.getName())
									.birthDate(childBasicInfoDto.getBirthDate())
									.note(childBasicInfoDto.getNote())
									.gender(childBasicInfoDto.getGender())
									.build();

				childRepository.save(_entity);
			}

		// 아이 정보 삭제하는 메소드
		@Transactional
		public void deleteById(Long childId) { childRepository.deleteById(childId); }


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
											String.format(
													"%s/%s",
													filePathUtil.childProfileImgUploadPath(),
													//여기서는 파일이름이 확장자를 포함하고 있다는 가정을합니다.
													relationShip.getChild().getSavedProfileImgName()
											)
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
		public ChildBasicInfoDTO selectBasicInfoById(Long id) {
			/*
				@TODO
				temp <- 이런 별 의미 없는 변수명의 사용은 최대한 지양해야합니다!!!
				조회 안되면 예외 던져야합니다!!!
			 */
			Optional<ChildEntity> temp = childRepository.findById(id);
			/*
				@TODO
				이렇게 null을 반환하는 코드는 절대로 안됩니다!!!
			 */
			if(temp.isPresent())
			{
				return ChildBasicInfoDTO.builder()
						.id(temp.get().getId())
						.name(temp.get().getName())
						.birthDate(temp.get().getBirthDate())
						.note(temp.get().getNote())
						.build();
			}
			return null;
		}
	}
