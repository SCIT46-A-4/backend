package com.scit.iLog.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scit.iLog.dto.HealthCheckListDTO;
import com.scit.iLog.dto.HealthCheckSaveDTO;
import com.scit.iLog.service.HealthCheckService;

import lombok.RequiredArgsConstructor;

/**
 * 영유아 건강 문진(HealthCheck) 관련 요청을 처리하는 Spring MVC 컨트롤러입니다.  
 * 사용자가 문진 결과를 등록, 조회, 삭제할 수 있도록 합니다.
 */
@Controller
@RequestMapping("/children")
@RequiredArgsConstructor
public class HealthCheckController {

	private final HealthCheckService healthCheckService;

	 /**
     * 문진 결과 등록 페이지 요청
     * 
     * 사용자가 새로운 문진 결과를 입력할 수 있도록 문진 결과 입력 페이지(/children/healthCheck/insertView.html)로 이동합니다.
     * 
     * @return "/children/healthCheck/insertView" (문진 결과 작성 페이지 경로)
     */
	@GetMapping("/healthCheck")
	public String handleGethealthCheckInsertView() {
		return "/children/healthCheck/insertView";
	}

	/**
     * 문진 결과 파일 업로드 및 저장 요청
     * 
     * 사용자가 입력한 문진 데이터를 저장한 후 문진 목록 페이지로 이동합니다.
     * 
     * @param healthCheckSaveDTO - 사용자가 입력한 문진 결과 데이터 (아이 ID, 멤버 ID, 파일 포함)
     * @param redirectAttributes - 성공 메시지를 전달하기 위한 객체
     * @return "redirect:/children/healthCheck/{healthCheckId}" (저장된 문진 결과 상세 페이지로 이동)
     */
	@PostMapping("/healthCheck")
	public String handleSaveHealthCheck(@ModelAttribute HealthCheckSaveDTO healthCheckSaveDTO,
			RedirectAttributes redirectAttributes) {

		// 서비스 호출 (예외 처리는 서비스에서)
		healthCheckService.saveHealthSurvey(healthCheckSaveDTO);

		redirectAttributes.addFlashAttribute("successMessage", "문진 결과가 성공적으로 등록되었습니다.");
		return "redirect:/children/healthCheck/{healthCheckId}";
	}

	/**
     * 문진 결과 목록 페이지 요청(우선 페이징 기능 제외)
     * 
     * 저장된 문진 결과 목록을 조회하여 문진 목록 페이지(/children/healthCheck/healthCheckListView.html)로 이동합니다.
     * 
     * @param offset - 조회 시작 위치
     * @param limit - 조회할 데이터 개수
     * @return "/children/healthCheck/healthCheckListView" (문진 목록 페이지 경로)
     */
	@GetMapping("/healthCheckList?offset=0&limit=10")
	public String handleGetHealthCheckListsView(@RequestParam(name = "offset") Long offset,
			@RequestParam(name = "limit") int limit, Model model) {

	    // 서비스에서 문진 결과 목록 조회
	    List<HealthCheckListDTO> healthCheckList = healthCheckService.getAllHealthCheckList();
	    
	    // 조회된 문진 결과 목록을 뷰에 전달
	    model.addAttribute("healthCheckList", healthCheckList);
	    
		return "/children/healthCheck/healthCheckListView";
	}

	/**
     * 특정 문진 결과 삭제 요청
     * 
     * 선택된 문진 결과를 삭제한 후 문진 목록 페이지로 이동합니다.
     * 
     * @param healthCheckId - 삭제할 문진 결과의 ID
     * @param rtrt - 페이지 리디렉션 시 사용할 파라미터 추가 객체
     * @return "redirect:/children/healthCheckList?offset=0&limit=10" (문진 목록 페이지로 이동)
     */
	@DeleteMapping("/healthCheck/{healthCheckId}")
	public String deleteHealthCheckList(@PathVariable(name = "healthCheckId") Long healthCheckId,
			RedirectAttributes rtrt) {
		
		// 서비스 호출하여 데이터 삭제
	    healthCheckService.deleteHealthSurvey(healthCheckId);
	    
		rtrt.addAttribute("offset", 0);
		rtrt.addAttribute("limit", 10);
		return "redirect:/children/healthCheckList?offset=0&limit=10";
	}
}
