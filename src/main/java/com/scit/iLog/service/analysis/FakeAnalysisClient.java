package com.scit.iLog.service.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import com.scit.iLog.dto.analysis.ai.AiAnalysisResponseDTO;
import com.scit.iLog.dto.analysis.ai.AiAnalysisResponseForWritingDTO;
import com.scit.iLog.dto.child.ChildRecordExtraction;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FakeAnalysisClient implements AnalysisClient {

    @Override
    public AiAnalysisResponseDTO getAiAnalysisResponse() {
        return AiAnalysisResponseDTO.builder()
                .analysisResult("아이가 즐겁게 뛰놈")
                .emotionScore(3)
                .emotionType(EmotionType.CONFUSED)
                .analysisResult("아이가 즐겁게 뛰놈")
                .build();
    }

    @Override
    public AiAnalysisResponseForWritingDTO getAiAnalysisResponseForWritings() {
        return AiAnalysisResponseForWritingDTO.builder()
                .analysisResult("밥을 먹어서 행복한듯 합니다.")
                .emotionScore(2)
                .emotionType(EmotionType.HAPPY)
                .build();
    }

    @Override
    public ChildRecordExtraction getChildRecordData(String childDataExtractionPrompt, MultipartFile healthCheckImg) {
        return new FakeChildRecordExtraction();
    }

    public static final class FakeChildRecordExtraction implements ChildRecordExtraction {

        @Override
        public double getHeight() {
            return 100;
        }

        @Override
        public double getWeight() {
            return 30;
        }

        @Override
        public double getLeftEye() {
            return 1.0;
        }

        @Override
        public double getRightEye() {
            return 0.8;
        }

        @Override
        public String getDiagnosis() {
            return "정상입니다.";
        }
    }
}
