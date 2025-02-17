package com.scit.iLog.util;

import lombok.extern.slf4j.Slf4j;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
public class DateTimeUtil {
    // 자주 사용되는 포맷터를 상수로 정의
    public static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter KOREAN_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
                    .withLocale(Locale.KOREAN);

    // 유틸리티 메서드
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    // null 안전 포맷팅 메서드
    public static String formatWithPattern(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return "";
        }
        try {
            return dateTime.format(DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeException e) {
            // 잘못된 패턴이 입력된 경우 기본 포맷 사용
            return dateTime.format(DATETIME_FORMATTER);
        }
    }

    public static String formatSafely(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return "";
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dateTime.format(formatter);
        } catch (DateTimeException e) {
            log.warn("날짜 포맷팅 실패. 기본 포맷 사용: {}", e.getMessage());
            return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}
