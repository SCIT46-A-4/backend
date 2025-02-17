package com.scit.iLog.dto;

import com.scit.iLog.dto.child.ChildProfileDTO;

import java.util.List;

public record ParentDashboardChildListDTO(
        int numberOfChildren,
        List<ChildProfileDTO> childProfileList
) {
}
