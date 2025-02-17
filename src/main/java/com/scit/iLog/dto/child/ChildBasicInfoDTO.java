package com.scit.iLog.dto.child;

import com.scit.iLog.domain.child.Gender;
import com.scit.iLog.dto.BaseTimeDTO;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildBasicInfoDTO extends BaseTimeDTO
{
    private Long id;
    private String name;
    private LocalDateTime birthDate;
    private String birthLocation;
    private String note; // 25/2/13 아직 안 쓰는 DTO
    private Gender gender;
    
	// 업로드되는 파일을 받기 위한 변수
	private MultipartFile profileImg;
}