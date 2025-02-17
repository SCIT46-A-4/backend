package com.scit.iLog.service.analysis;

import com.scit.iLog.dto.child.ChildRecordExtraction;
import org.springframework.web.multipart.MultipartFile;

public interface AnalysisService {
    ChildRecordExtraction extractData(String prompt, MultipartFile targetFile);
}
