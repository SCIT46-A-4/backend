package com.scit.iLog.dto.child;

import com.scit.iLog.domain.child.FamilyBackGround;
import com.scit.iLog.domain.child.Gender;
import com.scit.iLog.dto.BaseTimeDTO;
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
    private List<FamilyBackGround> familyBackGrounds;
}