package com.scit.iLog.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

// 공통 페이징 처리를 위한 DTO 입니다.
@Getter
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
