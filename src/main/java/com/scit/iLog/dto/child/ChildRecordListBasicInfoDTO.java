package com.scit.iLog.dto.child;

import com.scit.iLog.domain.child.FamilyBackGround;
import com.scit.iLog.domain.child.Gender;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ChildRecordListBasicInfoDTO(
        Long id,
        String name,
        LocalDateTime birthDate,
        String birthLocation,
        String note,
        Gender gender,
        String profileImgSrcUri,
        String callName,
        List<FamilyBackGround>familyBackGrounds
) {
}
