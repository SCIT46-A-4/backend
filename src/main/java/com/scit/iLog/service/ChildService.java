package com.scit.iLog.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.dto.ChildDTO;
import com.scit.iLog.repository.ChildRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildService
	{
		private final ChildRepository childRepository;

		private final String uploadPath = "c:/uploadPath";
		
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
				savedFileName    = FileService.saveFile(uploadFile, uploadPath);
				
				// 추출한 데이터를 다시 DTO에 넣는다!
				childDTO.setOriginalFileName(originalFileName);
				childDTO.setSavedFileName(savedFileName);
			} 
			
			//BoardEntity boardEntity = BoardEntity.toEntity(childDTO);
			
			//log.info("저장 : {} ", boardEntity.toString());
			
			//boardRepository.save(boardEntity);
		}
	}
