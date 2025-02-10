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

import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.service.ChildDiaryService;

@Controller
@RequestMapping("/children")
@RequiredArgsConstructor
public class DiaryController {
	private final ChildDiaryService childDiaryService;

	// 우리 아이 일기 통계로 보기 로 이동하기! = 우리 아이 일기장 버튼
    // 25/2/7 은진 : 주석 추가
    /**
     * 일기 통계 페이지 요청을 처리하는 컨트롤러 메서드.
     * 클라이언트(사용자)가 "/diaryStatistice" URL(버튼누를때)로 GET요청을 보낼 경우
     * @return '우리아이일기 통계로보기'페이지(diaryStatisticesView.html)로 이동
     */
    @GetMapping("/diaryStatistics")
    public String handleGetDiaryStatisticsView() {
        return "/children/diaries/diaryStatisticsView";
    }

    // 일기장 목록 페이지 요청 | 25/2/6 준성: 파라미터 Long id, Pageable 추가
    // 25/2/7 은진 : 주석 추가
    /**
     * 특정 아이의 일기장 목록 페이지 요청을 처리하는 컨트롤러 메서드.
     * 클라이언트(사용자)가 "/diaries" URL로 GET 요청을 보낼 경우 호출되며,
     * 요청 시 특정 아이의 ID와 페이징 정보를 함께 전달받는다.
     *
     * 특정 아이의 일기 목록을 페이징 처리하여 조회 :
     * Page<ChildDiaryEntity> _page = childDiaryService.getChildDiaries(id, pageable);
     *
     * 조회된 일기 목록을 모델에 추가하여 뷰에서 사용할 수 있도록 함 :
     * model.addAttribute("list", _page);
     * 
     * @param id 조회할 아이의 고유 iD
     * @param pageable 페이징 처리를 위한 pageable 객체
     *                 아래 코드에서는
     *                 기본적으로 첫 번째 페이지(page=0)와
     *                 10개의 항목(size=10)을 반환
     * @param model 뷰에 데이터를 전달하기 위한 Model 객체
     * @return "/children/diaries/diaryListView" 뷰 이름(일기 목록 화면 표시)
     */
    @GetMapping("/diaries")
    public String handleGetSelectAllDiaryList(
            @RequestParam Long id,
            @PageableDefault(page=0, size=10) Pageable pageable,
            Model model
    ) {
    	Page<ChildDiaryEntity> _page = childDiaryService.getChildDiaries(id, pageable);
    	model.addAttribute("list", _page);
        return "/children/diaries/diaryListView";
    }

    //일기장 쓰기 페이지
    //25/2/7 은진 : 주석 추가
    /**
     * 일기장 작성 페이지 요청을 처리하는 컨트롤러 메서드.
     * 클라이언트(사용자)가 "/diaries/new" URL(버튼)로 GET요청을 보낼 경우 호출되며,
     * 사용자가 새로운 일기를 작성할 수 있는 입력 폼 페이지를 반환한다.
     *
     * @return 해당 뷰를 통해 일기 작성 화면이 표시됨
     */
    @GetMapping("/diaries/new")
    public String handleGetDiaryInsertView() {
        return "children/diaries/insertView";
    }
}
