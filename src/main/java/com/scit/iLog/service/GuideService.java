package com.scit.iLog.service;

import com.scit.iLog.domain.GuideEntity;
import com.scit.iLog.dto.guide.GuideDTO;
import com.scit.iLog.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideService {
    private final GuideRepository guideRepository;

    // 모든 이용안내 목록 가져오기
    public List<GuideEntity> getAllGuides() {
        return guideRepository.findAll();
    }

    public List<GuideDTO> selectAll(String searchItem, String searchWord) {
        List<GuideEntity> guideEntityList = null;

        switch (searchItem) {
            case "title":
                guideEntityList = guideRepository.findByTitleContainingIgnoreCase(searchWord);
                break;

            case "content":
                guideEntityList = guideRepository.findByContentContainingIgnoreCase(searchWord);
                break;
        }

        List<GuideDTO> GuideDTOList = new ArrayList<>();

        for (var t : guideEntityList) {
            GuideDTO _data = GuideDTO.builder()
                    .id(t.getId())
                    .title(t.getTitle())
                    .content(t.getContent())
                    .build();

            GuideDTOList.add(_data);
        }

        return GuideDTOList;
    }
}
