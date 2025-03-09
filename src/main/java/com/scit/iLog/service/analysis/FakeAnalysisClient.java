package com.scit.iLog.service.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisTargetEntity;
import com.scit.iLog.domain.sentimentalAnalysis.AnalysisType;
import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import com.scit.iLog.dto.analysis.ai.AIAnalysisResponseDTO;
import com.scit.iLog.dto.analysis.ai.AIAnalysisResponseForWritingDTO;
import com.scit.iLog.dto.child.ChildRecordExtraction;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FakeAnalysisClient implements AnalysisClient {

    @Override
    public AIAnalysisResponseDTO getAIAnalysisResponse(AnalysisTargetEntity analysisTarget) {
        boolean hasNoWriting = analysisTarget.getAnalysisTargetTypes()
                .stream()
                .filter(analysisTargetType ->
                        analysisTargetType.getAnalysisType().getType() == AnalysisType.WRITING)
                .toList().isEmpty();
        String extractedText = hasNoWriting ? null : "추출된 텍스트";
        return AIAnalysisResponseDTO.builder()
                .emotionScore(3)
                .suggestedSolution("많이 놀아주세요")
                .emotionType(EmotionType.CONFUSED)
                .analysisResult("아이가 즐겁게 뛰놈")
                .extractedText(extractedText)
                .build();
    }

    @Override
    public AIAnalysisResponseForWritingDTO getAIAnalysisResponseForWritings() {
        return AIAnalysisResponseForWritingDTO.builder()
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
