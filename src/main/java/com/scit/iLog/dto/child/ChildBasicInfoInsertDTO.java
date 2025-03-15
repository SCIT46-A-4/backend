package com.scit.iLog.dto.child;

import com.scit.iLog.domain.child.FamilyBackGround;
import com.scit.iLog.domain.child.Gender;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

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
