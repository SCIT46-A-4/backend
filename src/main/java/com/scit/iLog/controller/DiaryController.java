package com.scit.iLog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scit.iLog.config.SecurityConfig.MemberDetails;
import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.dto.diary.ChildDiaryInsertDTO;
import com.scit.iLog.dto.diary.DiaryDetailsDTO;
import com.scit.iLog.dto.diary.DiaryUpdateDTO;
import com.scit.iLog.dto.diary.DiaryUpdateRequestDTO;
import com.scit.iLog.service.child.ChildDiaryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/children/{childId}/diaries")
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
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable(name="childId") Long childId,
            @PageableDefault(page=0, size=10) Pageable pageable,
            Model model
    ) {
    	Page<ChildDiaryEntity> childDiaryPage = childDiaryService.getChildDiaries(childId, pageable);
    	
    	model.addAttribute("list", childDiaryPage);
        model.addAttribute("memberName", memberDetails.getName());
        model.addAttribute("relationType", memberDetails.getRelationType().getTypeNameKr());
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
    @GetMapping("/new")
    public String handleGetDiaryInsertView(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("childId") Long childId,
            Model model
    ) {
        model.addAttribute("memberName", memberDetails.getName());
        model.addAttribute("relationType", memberDetails.getRelationType().getTypeNameKr());
    	model.addAttribute("childId", childId);
        return "children/diaries/diaryInsertView";
    }

    @PostMapping("/new")
    public String handlePostDiaryInsert(
            @PathVariable("childId") Long childId,
            @AuthenticationPrincipal MemberDetails memberDetails,
            @ModelAttribute ChildDiaryInsertDTO childDiaryInsertDTO
    ) {
        Long diaryId = childDiaryService.saveDiary(childId, memberDetails.getId(), childDiaryInsertDTO);
        return String.format("redirect:/children/%d/diaries/%d/details", childId, diaryId);
    }

    /*
		2025-02-10 이도훈
		/children/diaries/diaryDetailView를 출력 요청을 처리하는 메서드.
		CD-3
	*/
    @GetMapping("/{diaryId}/details")
    public String handleGetDiaryDetailView(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("childId") Long childId,
            @PathVariable("diaryId") Long diaryId,
            Model model
    ) {
        DiaryDetailsDTO diaryDetails = childDiaryService.findDiaryDetailsById(diaryId);
        model.addAttribute("diaryDetails", diaryDetails);
        model.addAttribute("memberName", memberDetails.getName());
        model.addAttribute("relationType", memberDetails.getRelationType().getTypeNameKr());
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
    @GetMapping("/{diaryId}/edit")
    public String handleGetDiaryUpdateView(
            @AuthenticationPrincipal MemberDetails memberDetails,
    		@PathVariable("childId") Long childId,
    		@PathVariable("diaryId") Long diaryId,
    		Model model
    ) {
    	DiaryUpdateDTO diary = childDiaryService.findDiaryUpdateInfoById(diaryId);
    	model.addAttribute("diary", diary);
    	model.addAttribute("child", childId);
        model.addAttribute("memberName", memberDetails.getName());
        model.addAttribute("relationType", memberDetails.getRelationType().getTypeNameKr());
    	return "children/diaries/diaryUpdateView";
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
            @PathVariable("childId") Long childId,
            @PathVariable("diaryId") Long diaryId,
    		@ModelAttribute DiaryUpdateRequestDTO diaryUpdateDTO
    ) {
    	childDiaryService.updateDiary(diaryId, diaryUpdateDTO);
    	return String.format("redirect:/children/%d/diaries/%d/details",childId,diaryId);
    }
    
    @ResponseBody
    @DeleteMapping("/{diaryId}")
    //@PathVariable("diaryId") Long diaryId는 URL경로에 포함된 diaryId라는 변수를 파라미터로 받는다.
    public String handleDeleteDiaryView(@PathVariable("diaryId") Long diaryId) {
        if (!childDiaryService.existsDiary(diaryId)) {
            return "fail"; // 존재하지 않는 경우
        }
        
        DiaryDetailsDTO diaryDetailsDto = childDiaryService.findDiaryDetailsById(diaryId);
        childDiaryService.deleteDiary(diaryId);
        return String.format("/children/%d/diaries/diaryList", diaryDetailsDto.childId());
    }
}
