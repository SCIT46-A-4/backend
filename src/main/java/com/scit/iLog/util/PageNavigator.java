package com.scit.iLog.util;

import lombok.Builder;
import lombok.Getter;

@Getter
public final class PageNavigator {
    //한 페이지에 레코드가 몇개인가
    private int itemsPerPage;
    //현재 페이지의 인덱스, 0-based indexing
    private int currentPageIndex;
    //전체 페이지의 개수
    private int totalPages;
    //페이징 네비게이션의 크기
    private int groupSize;
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

    private void calculatePagination() {
        this.itemCountPerGroup = itemsPerPage * groupSize;
        this.groupCount = totalPages / itemCountPerGroup;
        this.totalItemCount = itemsPerPage * totalPages;
        this.groupOfCurrentPage = currentPageIndex / groupSize;//0-based indexing
        this.firstPageIndexOfCurrentGroup = groupOfCurrentPage * 10 + 1;//1-based indexing
        this.lastPageIndexOfCurrentGroup = firstPageIndexOfCurrentGroup + groupSize - 1;
        this.firstPageIndexOfNextGroup = ((groupOfCurrentPage+1)*groupSize) + 1;
    }
}
