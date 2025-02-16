package com.scit.iLog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.dto.diary.DiaryUpdateDto;
import com.scit.iLog.service.child.ChildDiaryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/children")
@RequiredArgsConstructor
public class DiaryController {
	private final ChildDiaryService childDiaryService;

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
     *
     * CD-1
     */
    @GetMapping("/diaryList")
    public String handleGetSelectAllDiaryList(
            @RequestParam Long id,
            @PageableDefault(page=0, size=10) Pageable pageable,
            Model model
    ) {
        /*
            @TODO
            변수명 이렇게 적지 않도록 합시다!
            gpt가 해준건지 뭔지 모르겠지만...
            상황에도 맞지 않고 무엇보다 변수의 의도가 뭔지 모릅니다...
         */
    	Page<ChildDiaryEntity> _page = childDiaryService.getChildDiaries(id, pageable);
    	model.addAttribute("list", _page);
        return "children/diaries/diaryListView";
    }

    //일기장 수정 페이지 html작성과 로직
    //일기장 쓰기 페이지
    //25/2/7 은진 : 주석 추가
    /**
     * 일기장 작성 페이지 요청을 처리하는 컨트롤러 메서드.
     * 클라이언트(사용자)가 "/diaries/new" URL(버튼)로 GET요청을 보낼 경우 호출되며,
     * 사용자가 새로운 일기를 작성할 수 있는 입력 폼 페이지를 반환한다.
     *
     * @return 해당 뷰를 통해 일기 작성 화면이 표시됨
     * CD-2
     */
    @GetMapping("/diaries/new")
    public String handleGetDiaryInsertView() {
        return "children/diaries/insertView";
    }

    /*
		2025-02-10 이도훈
		/children/diaries/diaryDetailView를 출력 요청을 처리하는 메서드.
		CD-3
	*/
    @GetMapping("/{childId}/diaries/{diaryId}/details")
    public String handleGetDiaryDetailView(
            @PathVariable("childId") Long childId,
            @PathVariable("diaryId") Long diaryId,
            Model model
    ) {
        /*
            @TODO
            childId, diaryId로 일기를 조회해서 모델에 넣어줘야합니다.
         */
        return "children/diaries/diaryDetailsView";
    }

    /**
     * 2025-02-10 이도훈
     * ChildDiaryEntity의 diary_id를 값으로 갖고
     * 수정을 할 페이지를 조회 요청을 하는 메서드.
     * @param diaryId
     * @param model
     * @return
     *
     * CD-4
     */
    @GetMapping("/diaryList/{diaryId}/edit")
    public String handleGetDiaryUpdateView(
    		@RequestParam("diaryId") Long diaryId,
    		Model model
    ) {
    	DiaryUpdateDto diary = childDiaryService.findDiaryById(diaryId);

    	model.addAttribute("diary", diary);

    	return "children/diaries/updateView";
    }

    /**
     * 2025-02-10 이도훈
     * 수정 페이지에서 작업한 수정 내용 처리를 요청하는 메서드
     * @param diaryUpdateDTO
     * @return
     * CD-4
     */
    @PostMapping("/diaryList/{diaryId}/edit")
    public String handleUpdateDiaryUpdateView(
            /*
                여기 diaryId는 경로변수로 받는 것이 좋습니다.
             */
            @PathVariable("diaryId") Long diaryId,
    		@ModelAttribute DiaryUpdateDto diaryUpdateDTO
    ) {
    	childDiaryService.updateDiary(diaryId, diaryUpdateDTO);
    	return "children/diaries/updateView";
    }

    // 우리 아이 일기 통계로 보기 로 이동하기! = 우리 아이 일기장 버튼
    // 25/2/7 은진 : 주석 추가
    /**
     * 일기 통계 페이지 요청을 처리하는 컨트롤러 메서드.
     * 클라이언트(사용자)가 "/diaryStatistice" URL(버튼누를때)로 GET요청을 보낼 경우
     * @return '우리아이일기 통계로보기'페이지(diaryStatisticesView.html)로 이동
     * CD-X(미정)
     */
    @GetMapping("/diaryStatistics")
    public String handleGetDiaryStatisticsView() {
        return "children/diaries/diaryStatisticsView";
    }
}
