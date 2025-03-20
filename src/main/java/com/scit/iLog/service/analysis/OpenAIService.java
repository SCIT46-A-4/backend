package com.scit.iLog.service.analysis;


import com.scit.iLog.dto.analysis.ai.AIAnalysisResponseDTO;
import com.scit.iLog.dto.child.ChildRecordExtractionDTO;
import com.scit.iLog.exception.BadImageUrlException;
import com.scit.iLog.exception.FileConvertException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final ChatClient chatClient;

    @Value("${openAIApiModel}")
    private String defaultModel;

    @Value("${openAIApiMaxTokens}")
    private Integer maxTokens;

    public AIAnalysisResponseDTO getAIAnalysisResponse(final String filePath) {
        BeanOutputConverter<AIAnalysisResponseDTO> parser = new BeanOutputConverter<>(AIAnalysisResponseDTO.class);
        MimeType mimeType = (filePath.contains("jpeg") || filePath.contains("jpg")) ?
                MimeTypeUtils.IMAGE_JPEG : MimeTypeUtils.IMAGE_PNG;

        String response = chatClient.prompt()
                .user(userSpec -> {
                    try {
                        userSpec.text(
                                        """
                                                <role>
                                                너는 전문적인 아동 심리 분석가다.\s
                                                아이의 그림이나 아이 사진을 주면,\s
                                                해당 이미지를 분석하고,\s
                                                아래의 JSON 포맷(**AIAnalysisResponseDTO**)으로만 답변해야 한다.
                                                </role>
                                                
                                                <instruction>
                                                1. 응답은 반드시 유효한 JSON 구조여야 한다.
                                                2. 아래 5개 필드를 포함해야 한다:
                                                   - suggestedSolution (string)
                                                   - analysisResult (string)
                                                   - emotionScore (double)
                                                   - emotionType (enum: HAPPY, SAD, ANGRY, FEAR, SURPRISED, NEUTRAL, CONFUSED, ANXIOUS, BORED, EXCITED, SHY)
                                                   - extractedText (string)
                                                3. emotionScore 는 0.00 ~ 1.00 범위로 (소수점 둘째 자리까지) 나타낸다.\s
                                                   예: 0.35, 0.99
                                                4. emotionType 은 반드시 위 enum 목록 중 하나여야 한다.\s
                                                5. analysisResult 에는 아이가 표현한 감정 상태에 대한 설명을 2줄 이상의 한국어로 자유롭게 서술한다.
                                                6. suggestedSolution 에는 보호자 또는 교사에게 해줄 수 있는 상세한 조언을 3줄 이상의 한국어로 서술한다.
                                                7. extractedText 에는 이미지에서 추론하거나 인식된 텍스트를 문자열로 적는다.\s
                                                   (만약 추출된 텍스트가 없다면 빈 문자열 "")
                                                8. 절대로 JSON 외 불필요한 문장을 추가하지 말라.\s
                                                   예) “다음과 같은 JSON 입니다:” 처럼 JSON 밖의 설명이 있으면 안 된다.\s
                                                   오직 순수 JSON만 출력하라.
                                                </instruction>
                                                
                                                <example>
                                                예시 (값은 임의로 지어낸 것):
                                                {
                                                  "suggestedSolution": "아이와 함께 그림 속 캐릭터에 대해 이야기를 나누며 감정을 긍정적으로 표현하도록 유도해보세요.",
                                                  "analysisResult": "밝은 색감 사용과 웃는 표정으로 보아 즐거워 보이는 분위기지만, 약간의 긴장감도 엿보임.",
                                                  "emotionScore": 0.72,
                                                  "emotionType": "HAPPY",
                                                  "extractedText": "Happy day!"
                                                }
                                                </example>
                                                
                                                <format>
                                                public record AIAnalysisResponseDTO(
                                                        String suggestedSolution,
                                                        String analysisResult,
                                                        double emotionScore,
                                                        EmotionType emotionType,
                                                        String extractedText
                                                ) {
                                                }
                                                
                                                public enum EmotionType {
                                                    HAPPY("기쁨"),
                                                    SAD("슬픔"),
                                                    ANGRY("분노"),
                                                    FEAR("두려움"),
                                                    SURPRISED("놀람"),
                                                    NEUTRAL("중립"),
                                                    CONFUSED("혼란"),
                                                    ANXIOUS("불안"),
                                                    BORED("지루함"),
                                                    EXCITED("흥분"),
                                                    SHY("수줍음");
                                                }
                                                </format>
                                        """ + parser.getFormat())
                                .media(mimeType, convertFileToResource(filePath));
                    } catch (RuntimeException e) {
                        throw new BadImageUrlException();
                    }
                })
                .call()
                .content();
        return parser.convert(response);
    }

    public ChildRecordExtractionDTO getChildRecordExtractionResponse(MultipartFile healthCheckImg) {
        BeanOutputConverter<ChildRecordExtractionDTO> parser = new BeanOutputConverter<>(ChildRecordExtractionDTO.class);
        MimeType mimeType = (healthCheckImg.getOriginalFilename().contains("jpeg") || healthCheckImg.getOriginalFilename().contains("jpg")) ?
                MimeTypeUtils.IMAGE_JPEG : MimeTypeUtils.IMAGE_PNG;

        String response = chatClient.prompt()
                .user(userSpec -> {
                    try {
                        userSpec.text(
                                        """
                                                <role>
                                                너는 전문적인 아동 건강·발달 분석가다.
                                                아이의 건강검진 기록이나 문진표 이미지를 제공하면,
                                                다음 JSON 포맷(ChildRecordExtractionDTO) 형태로만 결과를 반환해야 한다.
                                                </role>
                                                
                                                <instruction>
                                                1. 응답은 반드시 올바른 JSON 구조여야 하며, 5개 필드만 포함한다:
                                                   - height (double)
                                                   - weight (double)
                                                   - leftEye (double)
                                                   - rightEye (double)
                                                   - diagnosis (string)
                                                
                                                2. height, weight, leftEye, rightEye는 숫자(소수점 포함)로 표현한다.
                                                   예) 120.55, 0.8, 15.0 등
                                                
                                                3. diagnosis는 아이가 검사받은 결과나 문진 의견을 간단히 서술한 문자열로 작성한다.
                                                   예) "키는 정상 범위, 체중은 다소 적음. 시력은 약간 저하."
                                                
                                                4. 절대로 JSON 외 불필요한 문장을 추가하지 말라.
                                                   (예: "다음과 같은 JSON입니다:" 등은 쓰지 말 것)
                                                   오직 순수 JSON만 출력하라.
                                                </instruction>
                                                
                                                <example>
                                                예시 (값은 임의로 지어낸 것):
                                                {
                                                  "height": 102.35,
                                                  "weight": 15.20,
                                                  "leftEye": 0.70,
                                                  "rightEye": 0.75,
                                                  "diagnosis": "키와 체중 모두 평균 범위. 시력은 약간 미흡하여 주의가 필요."
                                                }
                                                </example>
                                                
                                                <format>
                                                public record ChildRecordExtractionDTO(
                                                        double height,
                                                        double weight,
                                                        double leftEye,
                                                        double rightEye,
                                                        String diagnosis
                                                ) implements ChildRecordExtraction {
                                                    @Override
                                                    public double getHeight() { return height; }
                                                    @Override
                                                    public double getWeight() { return weight; }
                                                    @Override
                                                    public double getLeftEye() { return leftEye; }
                                                    @Override
                                                    public double getRightEye() { return rightEye; }
                                                    @Override
                                                    public String getDiagnosis() { return diagnosis; }
                                                }
                                                </format>
                                        """ + parser.getFormat())
                                .media(mimeType, convertMultipartFileToResource(healthCheckImg));
                    } catch (IOException e) {
                      throw new FileConvertException();
                    } catch (RuntimeException e) {
                        throw new BadImageUrlException();
                    }
                })
                .call()
                .content();
        return parser.convert(response);
    }

    public static Resource convertFileToResource(String filePath) {
        return new FileSystemResource(filePath);
    }

    /**
     * MultipartFile을 Resource로 변환합니다.
     *
     * @param file 변환할 MultipartFile
     * @return 변환된 Resource 객체
     * @throws IOException 파일 데이터를 읽어오는 중 발생할 수 있는 예외
     */
    private Resource convertMultipartFileToResource(MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }

}