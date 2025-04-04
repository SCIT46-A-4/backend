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

    public static Resource convertFileToResource(String filePath) {
        return new FileSystemResource(filePath);
    }

    public AIAnalysisResponseDTO getAIAnalysisResponse(final String childAdditionalInfo, final String filePath) {
        BeanOutputConverter<AIAnalysisResponseDTO> parser = new BeanOutputConverter<>(AIAnalysisResponseDTO.class);
        MimeType mimeType = (filePath.contains("jpeg") || filePath.contains("jpg")) ?
                MimeTypeUtils.IMAGE_JPEG : MimeTypeUtils.IMAGE_PNG;

        String promptText = """
                        <role>
                        You are a professional child psychology analyst.
                        When given a child’s drawing or photo,
                        you should analyze the image and respond
                        only in the following JSON format (**AIAnalysisResponseDTO**).
                        </role>
                
                        <instruction>
                        1. The response must be valid JSON.
                        2. Include the following 5 fields:
                           - suggestedSolution (string)
                           - analysisResult (string)
                           - emotionScore (double)
                           - emotionType (enum: HAPPY, SAD, ANGRY, FEAR, SURPRISED, NEUTRAL, CONFUSED, ANXIOUS, BORED, EXCITED, SHY)
                           - extractedText (string)
                
                        3. The emotionScore must be in the range of 0.00 to 1.00 (with two decimal places).
                           e.g., 0.35, 0.99
                
                        4. The emotionType must be one of the enum values listed above.
                
                        5. In analysisResult, freely write a detailed description (in at least 5 lines of Korean) about the child’s emotional state, including additional information about the child.
                
                        6. In suggestedSolution, provide detailed advice (in at least 10 lines of Korean) for parents or teachers, specifying at least three methods of how to respond verbally. Include further response methods as well, ensuring you first consider the child’s additional information.
                
                        7. In extractedText, include any text extracted from the image as a string.\s
                           (If no text is found, use an empty string "")
                
                        8. **Additional information about the child**: %s
                
                        9. Do not add any extraneous text outside of JSON.
                           Only output pure JSON.
                
                        10. If you cannot properly perform the analysis, you must clearly explain the detailed reasons why it cannot be performed.
                        </instruction>
                
                        <example>
                        Example:
                        {
                          "suggestedSolution": "Have a conversation with the child about the characters in the drawing, and encourage positive emotional expression.",
                          "analysisResult": "The child’s cheerful emotion is evident from the bright colors and smiling expression.",
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
                        ) {}
                
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
                """.formatted(childAdditionalInfo);

        String response = chatClient.prompt()
                .user(userSpec -> {
                    try {
                        userSpec.text(promptText)
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
                                                        이미지에서 필요한 데이터를 추출한 후,
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