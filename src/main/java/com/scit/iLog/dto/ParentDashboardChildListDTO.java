package com.scit.iLog.dto;

import java.util.List;

public record ParentDashboardChildListDTO(
        int numberOfChildren,
        List<ChildProfileDTO> childProfileList
) {
}
