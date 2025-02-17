package com.scit.iLog.service.analysis;

import com.scit.iLog.dto.child.ChildRecordExtraction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FakeAnalysisService implements AnalysisService {

    @Override
    public ChildRecordExtraction extractData(String prompt, MultipartFile targetFile) {
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
