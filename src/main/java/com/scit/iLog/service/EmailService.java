package com.scit.iLog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.scit.iLog.domain.permition.PermissionRequestDTO;
import com.scit.iLog.domain.permition.PermissionRequestEntity;
import com.scit.iLog.repository.PermissionRequestRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final PermissionRequestRepository permissionRequestRepository;
    /**
     * 인증번호를 포함한 이메일을 전송합니다.
     * @param to 수신자 이메일 주소
     * @param code 인증번호
     */
    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ilog@ilog.com");  // 발신자 주소
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("안녕하세요,\n\n귀하의 인증 코드는: " + code + "\n\n감사합니다.");
        mailSender.send(message);
    }
    
    /**
     * 권한 링크를 보내주는 함수
     * to		   : 이메일
     * guardionName: 보호자 이름
     * childName   : 아이이름
     * option	   : 비고, 추가란
     * **/
    public void sendAuthInviteEmail(String to, String guardionName, String childName, String... option)
    {
    	// DB에 저장하는 로직 필요
        SimpleMailMessage message = new SimpleMailMessage();
        String str = option.length < 1? "" : option[0];
        String token = UUID.randomUUID().toString();
        String verificationUrl = "http://localhost:8080/verify?token=" + token;
        
        message.setFrom("ilog@ilog.com");  // 발신자 주소
        message.setTo(to);
        message.setSubject(guardionName +"로부터" + childName + " 권한 초대 링크입니다.");
        message.setText("안녕하세요,\n\n"
        				+ childName + "의 인증 링크: \n" 
        				+ verificationUrl + "\n"
        				+ str);
        mailSender.send(message);
    }
    
    public boolean findInviteCode(String code)
    {
    	Optional<PermissionRequestEntity> resultEntity = permissionRequestRepository.findByRequestLinkCode(code);
    	
    	if(resultEntity.isPresent()) 
    		{
    			// 만약 존재하면 update 로직 필요
    			// 아래 거는 지워야 할 수 있음
    			PermissionRequestDTO dto = PermissionRequestDTO.builder()
    										.id(resultEntity.get().getId())
    										.childId(resultEntity.get().getChild().getId())
    										.requesterId(resultEntity.get().getRequester().getId())
    										.inviteeId(resultEntity.get().getInvitee().getId())
    										.relationType(resultEntity.get().getRelationType())
    										.permissionRequestStatus(resultEntity.get().getPermissionStatus())
    										.build();
    			
    			
    			return true;
    		}
    	else
    		return false;
    	
    }

    public static String generateVerificationCode() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        int code = 100000 + current.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
}

