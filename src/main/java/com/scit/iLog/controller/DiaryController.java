package com.scit.iLog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.dto.child.ChildSelectIdDto;
import com.scit.iLog.dto.diary.DiaryCreateDto;
import com.scit.iLog.dto.diary.DiaryIdSelectDto;
import com.scit.iLog.dto.diary.DiaryUpdateDto;
import com.scit.iLog.service.ChildDiaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/children")
@RequiredArgsConstructor
@Slf4j
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
    /**	API-43
     * 2025-02-11~13 이도훈
     * 준성씨가 작성하신 코드 수정 X
     * 접속 주소 : http://localhost:9900/children/diaries?id=3&page=0&size=10
     * @param childId
     * @param pageable
     * @param model
     * @return
     */
    @GetMapping("/diaries")
    public String handleGetSelectAllDiaryList(
            @RequestParam(name="id") Long childId,
            @PageableDefault(page=0, size=10) Pageable pageable,
            Model model
    ) {
    	Page<ChildDiaryEntity> _page = childDiaryService.selectChildDiaries(childId, pageable);
    	model.addAttribute("list", _page);
    	model.addAttribute("childId", childId);
        return "/children/diaries/diaryListView";
    }
    /**
     * API-44
     * 2025-02-11~13 이도훈
     * 삭제만 확인이 됨. 삭제는 기능 하지만 에러 발생. 테이블에 데이터 삭제 된 것 확인 완료
     * @param diaryId
     * @return
     */
    @ResponseBody
    @DeleteMapping("/diaries/{diaryId}")
    //@PathVariable("diaryId") Long diaryId는 URL경로에 포함된 diaryId라는 변수를 파라미터로 받는다.
    public String handleDeleteDiaryView(@PathVariable("diaryId") Long diaryId) {
        if (!childDiaryService.existsDiary(diaryId)) {
            return "fail"; // 존재하지 않는 경우
        }
        
        childDiaryService.deleteDiary(diaryId);
        Long childId = childDiaryService.updateDiarySelectChild(diaryId);
    	return "redirect:/children/diaries?id=" + childId  + "&offset=0&limit=10";
    }
    
//  /children/diaries?id=1&page=0&size=10
//일기장 수정 페이지 html작성과 로직
    //일기장 쓰기 페이지
    //25/2/7 은진 : 주석 추가
    /**
     * 일기장 작성 페이지 요청을 처리하는 컨트롤러 메서드.
     * 클라이언트(사용자)가 "/diaries/new" URL(버튼)로 GET요청을 보낼 경우 호출되며,
     * 사용자가 새로운 일기를 작성할 수 있는 입력 폼 페이지를 반환한다.
     *
     * @return 해당 뷰를 통해 일기 작성 화면이 표시됨
     */
    
    /**
     * API-45
     * 2025-02-11~13 이도훈
     * @param childId
     * @param model
     * @return
     */
    //일기 작성 페이지 호출
    @GetMapping("/{childId}/diaries/new")
    public String handleGetDiaryInsertView(
    		@PathVariable(name="childId") Long childId,
    		Model model) {
    	    
    	ChildSelectIdDto childSelectIdDto = childDiaryService.selectChildId(childId);
    	model.addAttribute("childId", childId);
    	
        return "children/diaries/insertView";
    }
    
    /**
     * API-46
     * 2025-02-11~13 이도훈
     * @param childId
     * @param diaryCreateDto
     * @return
     */
  //일기 작성 페이지의 데이터를 테이블에 저장
    @PostMapping("/{childId}/diaries/new")
    public String handleCreateDiaryInsertView(
    		@PathVariable(name="childId") Long childId,
    		@ModelAttribute DiaryCreateDto diaryCreateDto) {

    	Long authorId = childId;
    	childDiaryService.insertDiary(childId, authorId, diaryCreateDto);

    	return "redirect:/children/diaries?id=" + childId  + "&offset=0&limit=10";
    }
    
	/*
		2025-02-10 이도훈
		/children/diaries/diaryDetailView를 출력 요청을 처리하는 메서드.
	*/
    /**
     * API-47
     * 2025-02-11~13 이도훈
     * 일기 상세 보기 페이지 diaryDetailsView.html으로 이동
     * 일기장 목록에서 diaryId가 포함된 일기를 클릭하면 작동. 
     * selectChildDiaryId메서드를 통해 포함된 diaryId와 Entity의Id값이 동일하면 해당 데이터를 출력
     * @param diaryId
     * @param model
     * @return
     */
    @GetMapping("/diaries/{diaryId}")
    public String handleGetDaiaryDetailView(
    		@PathVariable(name="diaryId") Long diaryId,
    		Model model) {
    	
    	DiaryIdSelectDto diary = childDiaryService.selectChildDiaryId(diaryId);
    	
    	model.addAttribute("diary", diary);
    	
    	return "/children/diaries/diaryDetailsView";
    }
    
    /**
     * API-48
     * 2025-02-11~13 이도훈
     * ChildDiaryEntity의 dairy_id를 값으로 갖고
     * 수정을 할 페이지를 조회 요청을 하는 메서드.
     * @param diaryId
     * @param model
     * @return
     */
    @GetMapping("/diaries/{diaryId}/edit")
    public String handleGetDaiaryUpdateView(
    		@PathVariable(name="diaryId") Long diaryId,
    		Model model) {
    	
    	DiaryUpdateDto diaryUpdate = childDiaryService.selectUpdateChildDiary(diaryId);
    	model.addAttribute("diaryUpdate", diaryUpdate);
    	return "children/diaries/updateView";
    }

    /**
     * API-49
     * 2025-02-11~13 이도훈
     * 수정 페이지에서 작업한 수정 내용 처리를 요청하는 메서드
     * @param updateDto
     * @return
     */
    @PostMapping("/diaries/{diaryId}/edit")
    public String handleUpdateDaiaryUpdateView(
    		@ModelAttribute DiaryUpdateDto diaryUpdateDto,
    		@PathVariable(name="diaryId") Long diaryId) {
    	childDiaryService.updateDiaryDetail(diaryId, diaryUpdateDto);
    	
    	Long childId = childDiaryService.updateDiarySelectChild(diaryId);
    	
    	return "redirect:/children/diaries?id=" + childId  + "&offset=0&limit=10";
    }
}
