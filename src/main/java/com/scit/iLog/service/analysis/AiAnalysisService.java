package com.scit.iLog.service.analysis;

import com.scit.iLog.dto.child.ChildRecordExtraction;
import com.scit.iLog.dto.child.ChildRecordInsertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AiAnalysisService {
    private static final String childDataExtractionPrompt =
            "please extract height, weight, leftEye, rightEye, diagnosis of this child from this img";
    private final AnalysisService fakeAnalysisService;

    public ChildRecordInsertDTO extractChildRecordDataFromImg(MultipartFile healthCheckImg) {
        ChildRecordExtraction openAIChildRecordExtraction = fakeAnalysisService.extractData(
                childDataExtractionPrompt,
                healthCheckImg
        );
        return ChildRecordInsertDTO.builder()
                .height(openAIChildRecordExtraction.getHeight())
                .weight(openAIChildRecordExtraction.getWeight())
                .leftEye(openAIChildRecordExtraction.getLeftEye())
                .rightEye(openAIChildRecordExtraction.getRightEye())
                .note(openAIChildRecordExtraction.getDiagnosis())
                .build();
    }
}
