package com.scit.iLog.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

// 페이징 응답을 위한 DTO
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
