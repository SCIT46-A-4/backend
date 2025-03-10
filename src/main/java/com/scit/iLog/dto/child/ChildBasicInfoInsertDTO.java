package com.scit.iLog.dto.child;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.scit.iLog.domain.child.FamilyBackGround;
import com.scit.iLog.domain.child.Gender;

public record ChildBasicInfoInsertDTO(
        String name,
        LocalDate birthDate,
        String birthLocation,
        String note,
        Gender gender,
        MultipartFile profileImg,
        String callName,
        List<FamilyBackGround> familyBackGrounds
) {
}
