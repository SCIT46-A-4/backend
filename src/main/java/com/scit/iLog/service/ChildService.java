package com.scit.iLog.service;
import java.util.List;
import java.util.Optional;

import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.dto.ChildProfileDTO;
import com.scit.iLog.dto.ParentDashboardChildListDTO;
import com.scit.iLog.repository.RelationShipRepository;
import com.scit.iLog.util.FilePathUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.dto.ChildDTO;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.util.FileManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildService
	{
		private final ChildRepository childRepository;
		private final RelationShipRepository relationShipRepository;
		private final FileManager fileManager;
		private final FilePathUtil filePathUtil;

		// 25/2/12 준: api-23 아이 새로운 아이 등록(저장)
		public Long save(ChildDTO childDto)
			{
				// 25/2/12 ㅈ: child 저장 후 저장된 id값 반환
				ChildEntity entity = ChildEntity.builder()
									.gender(childDto.getGender())
									.name(childDto.getName())
									.birthDate(childDto.getBirthDate())
									.build();
				
				ChildEntity _child = childRepository.save(entity);
				
				return _child.getId();
			}
		
		public void insertBoard(ChildDTO childDTO) {
			MultipartFile uploadFile = childDTO.getUploadFile();
			String originalFileName = null;
			String savedFileName = null;
			
			if(!uploadFile.isEmpty()) {   // 첨부된 파일이 있는 경우
				originalFileName = uploadFile.getOriginalFilename();
				savedFileName    = fileManager.saveFile(uploadFile, filePathUtil.childProfileImgUploadPath());
				
				// 추출한 데이터를 다시 DTO에 넣는다!
				childDTO.setOriginalFileName(originalFileName);
				childDTO.setSavedFileName(savedFileName);
			} 
			
			//BoardEntity boardEntity = BoardEntity.toEntity(childDTO);
			
			//log.info("저장 : {} ", boardEntity.toString());
			
			//boardRepository.save(boardEntity);
		}

		//25/2/13 준: api-??: 아이 정보 찾아서 반환
		public ChildDTO findById(Long id) throws Exception
			{
				// _entity 받아오는 함수
				Optional<ChildEntity> _entity = childRepository.findById(id);
				if(!_entity.isPresent())
					{
						throw new Exception("파일을 찾을 수 없습니다");
					}
			
				// 아이 정보 체크 받아오기(함수에 넣으면 entity -> childDTO로 변경)
				return convertToChildDTO(_entity.get());
			}
		
		
		// child entity를 빌더를 이용해서 DTO로 반환 로컬에서 쓸 함수 
		private ChildDTO convertToChildDTO(ChildEntity _entity)
		{
			return new ChildDTO().builder()
					.id(_entity.getId())
					.name(_entity.getName())
					.birthDate(_entity.getBirthDate())
					.gender(_entity.getGender())
					.build();
		}

		// child의 정보를 수정하는 함수
		public void updateChildData(Long childId, ChildDTO childDto)
			{
				ChildEntity _entity = ChildEntity.builder()
									.id(childId)
									.name(childDto.getName())
									.birthDate(childDto.getBirthDate())
									.note(childDto.getNote())
									.gender(childDto.getGender())
									.build();
				
				childRepository.save(_entity);
			}

		// 아이 정보 삭제하는 메소드
		public void deleteById(Long childId) { childRepository.deleteById(childId); }


		public ParentDashboardChildListDTO getChildrenProfilesOf(Long memberId) {
			List<RelationShipEntity> relationShips = relationShipRepository.findAllByMemberId(memberId);
			List<ChildProfileDTO> childProfiles = relationShips.stream()
					.map(relationShip ->
							ChildProfileDTO.builder()
									.id(relationShip.getChild().getId())
									.birthDate(relationShip.getChild().getBirthDate())
									.name(relationShip.getChild().getName())
									.profileImgSrc(
											filePathUtil.childProfileImgUploadPath() +
													"/" +
													relationShip.getChild().getSavedProfileImgName()
									)
									.build()
					)
					.toList();
			return new ParentDashboardChildListDTO(
					childProfiles.size(),
					childProfiles
			);
		}
    }
