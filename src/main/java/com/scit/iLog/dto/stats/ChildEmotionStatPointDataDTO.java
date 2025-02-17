package com.scit.iLog.dto.stats;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChildEmotionStatPointDataDTO(
        LocalDateTime date,     // x축 날짜
        double resultScore,        // y축 값
        String label,           // 툴팁이나 마커에 표시할 레이블
        String detailUrl        //마커 누르면 이동할 주소
) {
}
