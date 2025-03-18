package com.scit.iLog.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AgeCalculator {
    /**
     * 생년월일 문자열("yyyy-MM-dd" 형식)을 받아서 현재의 만 나이를 계산합니다.
     *
     * @param birthDateString 생년월일 (예: "2000-05-15")
     * @return 만 나이 (정수)
     */
    public static int calculateAge(String birthDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(birthDateString, formatter);
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears();
    }

    /**
     * LocalDateTime 형태의 생년월일을 받아서 현재의 만 나이를 계산합니다.
     *
     * @param birthDateTime 생년월일 (LocalDateTime)
     * @return 만 나이 (정수)
     */
    public static int calculateAge(LocalDateTime birthDateTime) {
        LocalDate birthDate = birthDateTime.toLocalDate();
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears();
    }
}
