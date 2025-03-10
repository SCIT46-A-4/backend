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
        String verificationUrl = "http://localhost:8080/verifyLink?token=" + token;
        
        message.setFrom("ilog@ilog.com");  // 발신자 주소
        message.setTo(to);
        message.setSubject(guardionName +"로부터" + childName + " 권한 초대 링크입니다.");
        message.setText("안녕하세요,\n\n"
        				+ childName + "의 인증 링크: \n" 
        				+ verificationUrl + "\n"
        				+ str);
        mailSender.send(message);
        
        // DB에 해당 사항을 하나 만들어서 save 하는 로직 필요.
    }
    
    // 초대 받은 사람이 링크 클릭시, 이것이 db에 있는지 확인하고 업데이트 하는 로직
    public void findInviteCodeAndUpdate(String code) throws Exception
    {
    	// null을 검색하면 문제 다른 유저 것도 검색될 수 있어서 null 체크로 방지
    	if(code == null) throw new IllegalArgumentException("받은 링크가 null입니다.");
    	
    	Optional<PermissionRequestEntity> resultEntity = 
    			permissionRequestRepository.findByRequestLinkCode(code);
    	
    	resultEntity.orElseThrow(() -> new Exception("링크가 같은 것을 DB에서 찾지 못했습니다."));
    	
    	// 존재하면 update
    	resultEntity.get().setPermissionStatusAndDeleteRequestLinkCode(PermissionRequestStatus.ACCEPTED);	
    }

    public static String generateVerificationCode() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        int code = 100000 + current.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
    
    public void savePermissionEntity(PermissionRequestDTO dto)
    {
    	Optional<MemberEntity> requesterEntity = memberRepository.findById(dto.requesterId());  // 요청보낸사람
    	Optional<MemberEntity> inviteeEntity   = memberRepository.findById(dto.inviteeId());	// 초대받은사람
    	Optional<ChildEntity>  childEntity     = childRepository.findById(dto.childId());	 	// 초대받은사람
    	
    	PermissionRequestEntity _entity = 
    			PermissionRequestEntity.builder()
    			.requester(requesterEntity.get())
    			.child(childEntity.get())
    			.invitee(requesterEntity.get())
    			.relationType(dto.relationType())
    			.permissionStatus(dto.permissionRequestStatus())
    			.requestLinkCode(dto.requestCodeLink())
    			.build();
    }
}

