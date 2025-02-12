package com.scit.iLog.dto;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.child.Gender;
import lombok.*;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildDTO extends BaseTimeDTO 
{
    private Long id;
    private String name;
    private LocalDateTime birthDate;
    private String note;
    private Gender gender;
    
	// 업로드되는 파일을 받기 위한 변수
	private MultipartFile uploadFile;
	
	// 파일명(2종류)
	private String originalFileName;
	private String savedFileName;
}
