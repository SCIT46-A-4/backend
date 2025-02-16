package com.scit.iLog.service.analysis;

import com.scit.iLog.dto.analysis.OpenAIChildRecordExtraction;
import org.springframework.web.multipart.MultipartFile;

public class OpenAIAPIService implements AnalysisService {

    public OpenAIChildRecordExtraction extractData(String s, MultipartFile healthCheckImg) {
        //@TODO 나중에 실제로 OpenAI api를 호출하는 코드로 바꿔야합니다.
        return null;
    }
}
