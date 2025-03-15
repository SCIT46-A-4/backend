package com.scit.iLog.dto.child;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scit.iLog.domain.child.FamilyBackGround;
import com.scit.iLog.domain.child.Gender;
import com.scit.iLog.dto.BaseTimeDTO;
import com.scit.iLog.util.FamilyBackGroundSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildBasicInfoDTO extends BaseTimeDTO {
    private Long id;
    private String name;
    private LocalDateTime birthDate;
    private String birthLocation;
    private String note;
    private Gender gender;
    private String profileImgSrcUri;
    private String callName;

    //이도훈 추가, util패키지의 FamilyBackGroundSerializer클래스 추가
    @JsonSerialize(using = FamilyBackGroundSerializer.class)
    private List<FamilyBackGround> familyBackGrounds;
}
