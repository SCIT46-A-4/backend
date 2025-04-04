package com.scit.iLog.util;

import lombok.Builder;
import lombok.Getter;

@Getter
public final class PageNavigator {
    //한 페이지에 레코드가 몇개인가
    private final int itemsPerPage;
    //현재 페이지의 인덱스, 0-based indexing
    private final int currentPageIndex;
    //전체 페이지의 개수
    private final int totalPages;
    //페이징 네비게이션의 크기
    private final int groupSize;
    //하나의 페이징 네비게이션그룹의 전체 아이템 개수
    private int itemCountPerGroup;
    //페이징 네비게이션 그룹의 개수
    private int groupCount;
    //전체 레코드의 개수
    private int totalItemCount;
    //현재 페이지가 속한 페이징 네비게이션 그룹, 0-based indexing
    private int groupOfCurrentPage;
    //현재 그룹의 첫번째 페이지의 인덱스, 1-based indexing
    private int firstPageIndexOfCurrentGroup;
    //현재 그룹의 마지막 페이지의 인덱스
    private int lastPageIndexOfCurrentGroup;
    //다음 그룹의 첫 페이지
    private int firstPageIndexOfNextGroup;

    @Builder(builderMethodName = "of")
    public PageNavigator(int groupSize, int itemsPerPage, int currentPage, int totalPages) {
        this.groupSize = groupSize;
        this.itemsPerPage = itemsPerPage;
        this.currentPageIndex = currentPage;
        this.totalPages = totalPages;
        calculatePagination();
    }

    /**
     * C-6 이도훈 2025-02-24~26
     */
    private void calculatePagination() {
        this.itemCountPerGroup = itemsPerPage * groupSize;
        // 그룹 개수 계산 (올림 처리)
        this.groupCount = (totalPages + groupSize - 1) / groupSize;
        this.totalItemCount = itemsPerPage * totalPages;
        this.groupOfCurrentPage = currentPageIndex / groupSize;//0-based indexing
        // 현재 그룹의 첫 번째 페이지 (0-based index)
        this.firstPageIndexOfCurrentGroup = groupOfCurrentPage * groupSize;
        // 현재 그룹의 마지막 페이지 (최대 totalPages - 1을 초과하지 않도록 처리)
        this.lastPageIndexOfCurrentGroup = Math.min(firstPageIndexOfCurrentGroup + groupSize - 1, totalPages - 1);
        // 다음 그룹의 첫 번째 페이지 (초과하면 -1 설정)
        this.firstPageIndexOfNextGroup = (groupOfCurrentPage + 1) * groupSize;
        if (this.firstPageIndexOfNextGroup >= totalPages) {
            this.firstPageIndexOfNextGroup = -1; // 다음 그룹이 없을 경우 -1로 설정
        }
    }
}
