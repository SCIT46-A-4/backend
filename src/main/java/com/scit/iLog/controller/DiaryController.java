package com.scit.iLog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scit.iLog.domain.ChildDiaryEntity;
import com.scit.iLog.service.ChildDiaryService;

@Controller
@RequestMapping("/children")
@RequiredArgsConstructor
public class DiaryController {
	private final ChildDiaryService childDiaryService;

	// 우리 아이 일기 통계로 보기 로 이동하기! = 우리 아이 일기장 버튼
    @GetMapping("/diaryStatistics")
    public String diaryStatistics() {
        return "/children/diaryStatisticsView";
    }

    // 일기장 목록 페이지 요청 | 25/2/6 준성: 파라미터 Long id, Pageable 추가 
    @GetMapping("/diaries")
    public String selectAll(
            @RequestParam Long id,
            @PageableDefault(page=0, size=10) Pageable pageable,
            Model model
    ) {
    	Page<ChildDiaryEntity> _page = childDiaryService.getChildDiaries(id, pageable);
    	
    	model.addAttribute("list", _page);
        return "/children/diaries/diaryListView";
    }

    //일기장 쓰기 페이지
    @GetMapping("/diaries/new")
    public String handleInsertDiary() {
        return "children/diaries/insertView";
    }
}
