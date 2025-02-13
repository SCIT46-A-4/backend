package com.scit.iLog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckSaveDTO {
    private Long childId;  // 아이 ID
    private Long memberId; // 멤버 ID
    private MultipartFile surveyFile; // 업로드할 파일
}