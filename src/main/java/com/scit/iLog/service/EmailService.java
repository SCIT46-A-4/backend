package com.scit.iLog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.permition.PermissionRequestDTO;
import com.scit.iLog.domain.permition.PermissionRequestEntity;
import com.scit.iLog.domain.permition.PermissionRequestStatus;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.MemberRepository;
import com.scit.iLog.repository.PermissionRequestRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final PermissionRequestRepository permissionRequestRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    /**
     * 인증번호를 포함한 이메일을 전송합니다.
     * @param to 수신자 이메일 주소
     * @param code 인증번호
     */
    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("ilog@ilog.com");  // 발신자 주소
        message.setFrom("aaaTesT255@gmail.com");
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        
        message.setText("안녕하세요,\n\n귀하의 인증 코드는: " + code + "\n\n감사합니다.");
        
        log.info("이메일 전송 시도: to={}, code={}", to, code);
        mailSender.send(message);
        log.info("이메일 전송 완료: to={}", to);
    }
    
    /**
     * 권한 링크를 보내주는 함수
     * to		   : 이메일
     * guardianName: 보호자 이름
     * childName   : 아이이름
     * option	   : 비고, 추가란
     * **/
    // void에서 PermissionRequestEntity로 변경
    public PermissionRequestEntity  sendAuthInviteEmail(
    		String to,
    		String guardianName,
    		Long childId, 
    		Long requesterId,
    		String _alias,
    		String inviteeEmail,
    		String etc)
    {
    	Optional<MemberEntity> requesterEntity = memberRepository.findById(requesterId);
    	inviteeEmail = requesterEntity.get().getEmail();
    	
    	// DB에 저장하는 로직 필요
        SimpleMailMessage message = new SimpleMailMessage();
//        String str = etc.length() < 1? "" : etc;
        String str = (etc == null || etc.length() < 1) ? "" : etc; //임시
        String token = UUID.randomUUID().toString();
        String verificationUrl = "http://localhost:9900/verifyLink?token=" + token;
        
//        message.setFrom("ilog@ilog.com");  // 발신자 주소
        message.setFrom(inviteeEmail);  // 발신자 주소
        message.setTo(to);
        message.setSubject(guardianName + "로부터" + " 권한 초대 링크입니다.");
        message.setText("안녕하세요,\n\n"
        				+ "인증 링크: \n" 
        				+ verificationUrl + "\n"
        				+ str);
        
        log.info("생성된 토큰: {}", token);
        log.info("생성된 인증 URL: {}", verificationUrl);
        System.out.println("===========================================");
        System.out.println("1");
        mailSender.send(message);
        System.out.println("===========================================");
        System.out.println("2");        
        // DB에 해당 사항을 하나 만들어서 save 하는 로직 필요.
        PermissionRequestDTO permissionRequestDto = 
        		PermissionRequestDTO.builder()
        		.alias(_alias)
        		.childId(childId)
        		.requesterId(requesterId)
        		.inviteeId(requesterId)
        		.relationType(RelationType.TEACHER)
        		.permissionRequestStatus(PermissionRequestStatus.PENDING)
        		.requestCodeLink(token)
        		.build();
        
        System.out.println("===========================================");
        System.out.println("3");  
//        Optional<MemberEntity> requesterEntity = memberRepository.findById(permissionRequestDto.getRequesterId());	// 요청보낸사람
        Optional<MemberEntity> inviteeEntity = memberRepository.findById(permissionRequestDto.getInviteeId());		// 초대받은사람
        childRepository .findById(permissionRequestDto.getChildId());
        System.out.println("===========================================");
        System.out.println("4");  
        
        return savePermissionEntity(permissionRequestDto);
    }
    
    // 초대 받은 사람이 링크 클릭시, 이것이 db에 있는지 확인하고 업데이트 하는 로직
    public void findInviteCodeAndUpdate(String code) throws Exception
    {
    	// null을 검색하면 문제 다른 유저 것도 검색될 수 있어서 null 체크로 방지
    	if(code == null) throw new IllegalArgumentException("받은 링크가 null입니다.");
    	
    	// 저장된 코드 찾기
    	Optional<PermissionRequestEntity> resultEntity = 
    			permissionRequestRepository.findByRequestLinkCode(code);
    	
    	resultEntity.orElseThrow(() -> new Exception("링크가 같은 것을 DB에서 찾지 못했습니다."));
    	
    	// 3분 이내인지 체크
    	boolean isTimeLimit = checkInTimeLimit(resultEntity.get().getModifiedAt(), 3);
    	resultEntity.get().setRequestLinkCode(null);	// 링크 초기화
    	
    	if(!isTimeLimit)
    			throw new Exception("email 제한 시간을 넘겼습니다. 코드가 유효하지 않습니다. 코드를 다시 보내주세요.");
    	
    	// 코드 받은 사람이 있고, 제한 시간 안에 들어왔다면 update
    	resultEntity.get().setPermissionStatusAndDeleteRequestLinkCode(PermissionRequestStatus.ACCEPTED);
    }

    public static String generateVerificationCode() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        int code = 100000 + current.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
    
 // void에서 PermissionRequestEntity로 변경
    public PermissionRequestEntity  savePermissionEntity(PermissionRequestDTO dto)
    {
    	Optional<MemberEntity> requesterEntity = memberRepository.findById(dto.getRequesterId());  // 요청보낸사람
    	Optional<MemberEntity> inviteeEntity   = memberRepository.findById(dto.getInviteeId());	// 초대받은사람
    	Optional<ChildEntity>  childEntity     = childRepository.findById(dto.getChildId());	 	// 초대받은사람
    	
    	PermissionRequestEntity _entity = 
    			PermissionRequestEntity.builder()
    			.requester(requesterEntity.get())
    			.child(childEntity.get())
    			.invitee(requesterEntity.get())
    			.relationType(dto.getRelationType())
    			.permissionStatus(dto.getPermissionRequestStatus())
    			.requestLinkCode(dto.getRequestCodeLink())
    			.alias(dto.getAlias())
    			.build();
    	return permissionRequestRepository.save(_entity);
    }
    
    public boolean checkInTimeLimit(LocalDateTime time, long limitTime)
    {
    	// 제한 시간 안이면 true, 제한시간 오버됐으면 false
    	boolean result = false;
    	if(Duration.between(time, LocalDateTime.now()).toMinutes() <= limitTime) result = true;
    	else result = false;
    	
    	return result;
    	
    }
}

