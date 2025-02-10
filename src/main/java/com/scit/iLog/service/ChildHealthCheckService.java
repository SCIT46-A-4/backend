package com.scit.iLog.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.scit.iLog.domain.HealthSurveyEntity;
import com.scit.iLog.repository.ChildHealthCheckRepository;
import com.scit.iLog.repository.ChildRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildHealthCheckService
	{
		private final ChildRepository childRepository;
		private final ChildHealthCheckRepository childHealthCheckRepository;	// 25/2/10 준 : 영유아 검진 레포
		
		// 24/2/10 준성 api-43 영유아 건강문진 결과 삭제요청
 		public boolean deleteCheckList(Long id)
			{
				try
					{
						// 25/2/10 준 : 데이터 찾아서 삭제하는 로직
						Optional<HealthSurveyEntity> entity = childHealthCheckRepository.findById(id);
						
						if(entity.isPresent())
							{
								childHealthCheckRepository.delete(entity.get());
								return true;
							}
						
						return false;
					}
				
				catch (Exception e)
					{
						log.error("childHealthDelete 삭제 중 에러 발생:" + e.getMessage() + "\n" + e.getCause());
						return false;
					}

			}
	}
