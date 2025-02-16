package com.scit.iLog.domain;

import lombok.Getter;

@Getter
public enum PeriodType {
    DAY("일간"),
    WEEK("주간"),
    MONTH("월간");

    private final String description;

    PeriodType(String description) {
        this.description = description;
    }
}
