package com.scit.iLog.dto.child;

import com.scit.iLog.domain.child.FamilyBackGround;
import com.scit.iLog.domain.child.Gender;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ChildBasicInfoUpdateDTO(
                String name,
//                @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // 2025-02-28 / 김은진 / 날짜 형식 변환
                LocalDate birthDate,
                String birthLocation,
                String note,
                Gender gender,
                // MultipartFile profileImg,
                String callName,
                List<FamilyBackGround> familyBackGrounds,
                // 2025-02-28 / 김은진 / 이미지는 마지막에 위치하고 명시적으로 null 허용
                @Nullable MultipartFile profileImg
) {
}
