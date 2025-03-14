package com.scit.iLog.service;

import com.scit.iLog.domain.PermissionLevel;
import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.permition.PermissionRequestDTO;
import com.scit.iLog.domain.permition.PermissionRequestEntity;
import com.scit.iLog.domain.permition.PermissionRequestStatus;
import com.scit.iLog.dto.auth.PermissionTeacherDTO;
import com.scit.iLog.exception.MemberNotFoundException;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.MemberRepository;
import com.scit.iLog.repository.PermissionRequestRepository;
import com.scit.iLog.repository.RelationShipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final MemberRepository memberRepository;
    private final PermissionRequestRepository permissionRequestRepository;
    private final ChildRepository childRepository;
    private final RelationShipRepository relationShipRepository;

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
     * guardianName: 보호자 이름
     * childName   : 아이이름
     * option	   : 비고, 추가란
     * @throws Exception
     * **/
    // void에서 PermissionRequestEntity로 변경
    @Transactional
    public PermissionRequestEntity sendAuthInviteEmail(
            String to, //현재 로그인한 멤버의 이메일
            String guardianName,
            Long childId,
            Long requesterId,
            String _alias,
            String inviteeEmail, // 사용자가 뷰에서 입력한 이메일 (수신자)
            String etc) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        //Null처리
        String str = (etc == null || etc.length() < 1) ? "" : etc;
        String token = UUID.randomUUID().toString();
        String verificationUrl = "http://localhost:9900/verifyLink?token=" + token;
        message.setFrom("aaaTesT255@gmail.com"); // 발신자 주소 이도훈email
        message.setTo(inviteeEmail);
        message.setSubject(guardianName + "로부터" + " 권한 초대 링크입니다.");
        message.setText("안녕하세요,\n\n"
                + "인증 링크: \n"
                + verificationUrl + "\n"
                + str);

        log.info("생성된 토큰: {}", token);
        log.info("생성된 인증 URL: {}", verificationUrl);
        mailSender.send(message);

        Long inviteeId = memberRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new MemberNotFoundException(inviteeEmail)).getId();

        if(inviteeId == null) {
            throw new Exception("초대받을 회원이 존재하지 않습니다.");
        }

        // DB에 해당 사항을 하나 만들어서 save 하는 로직 필요.
        PermissionRequestDTO permissionRequestDto = PermissionRequestDTO.builder()
                .alias(_alias)
                .childId(childId)
                .requesterId(requesterId)
                .inviteeId(inviteeId)
                .requesterRelationType(RelationType.TEACHER)
                .permissionRequestStatus(PermissionRequestStatus.PENDING)
                .requestCodeLink(token)
                .build();

        Optional<MemberEntity> requesterEntity = memberRepository.findById(permissionRequestDto.getRequesterId()); // 요청보낸사람
        Optional<MemberEntity> inviteeEntity = memberRepository.findById(permissionRequestDto.getInviteeId()); // 초대받은사람

        childRepository.findById(permissionRequestDto.getChildId());

        return savePermissionEntity(permissionRequestDto);
    }

    // 초대 받은 사람이 링크 클릭시, 이것이 db에 있는지 확인하고 업데이트 하는 로직
    @Transactional
    public void findInviteCodeAndUpdate(String code) throws Exception {
        // null을 검색하면 문제 다른 유저 것도 검색될 수 있어서 null 체크로 방지
        if (code == null)
            throw new IllegalArgumentException("받은 링크가 null입니다.");

        // 저장된 코드 찾기
        PermissionRequestEntity resultEntity = permissionRequestRepository.findByRequestLinkCode(code)
                .orElseThrow(() -> new Exception("링크가 같은 것을 DB에서 찾지 못했습니다."));;

        // 3분 이내인지 체크
        boolean isTimeLimit = checkInTimeLimit(resultEntity.getModifiedAt(), 3);
        resultEntity.setRequestLinkCode(null); // 링크 초기화

        if (!isTimeLimit)
            throw new Exception("email 제한 시간을 넘겼습니다. 코드가 유효하지 않습니다. 코드를 다시 보내주세요.");

        // 코드 받은 사람이 있고, 제한 시간 안에 들어왔다면 update
        resultEntity.setPermissionStatusAndDeleteRequestLinkCode(PermissionRequestStatus.ACCEPTED);
        saveRelationShipEntity(resultEntity);
    }

    public static String generateVerificationCode() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        int code = 100000 + current.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    /**
     * 2025-03-11~12
     *
     * ✅ 아이디 찾기 이메일 전송
     * 사용자가 입력한 이메일 주소로 해당 계정의 아이디를 전송합니다.
     *
     * @param to - 이메일을 받을 사용자 주소
     * @param id - 사용자의 아이디 (회원가입 시 등록한 로그인 아이디)
     */
    public void sendIdFindEmail(String to, String id) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("helper@ilog.com");  // 발신자 주소
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("안녕하세요, 회원님, \n\n귀하의 아이디는: 「 " + id + "」 입니다. \n\n감사합니다.");
        mailSender.send(message);
    }

    /**
     * 2025-03-11~12
     *
     * ✅ 임시 비밀번호 전송
     * 사용자의 요청에 따라 새로운 임시 비밀번호를 생성하여 이메일로 전송합니다.
     *
     * @param to - 이메일을 받을 사용자 주소
     * @param newPwd - 새롭게 발급된 임시 비밀번호 (사용자는 로그인 후 변경 필요)
     */
    public void sendPwdFindEmail(String to, String newPwd) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("helper@ilog.com");  // 발신자 주소
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("안녕하세요, 회원님, \n\n귀하의 비밀번호는: 「" + newPwd + "」 입니다. \n\n감사합니다.");
        mailSender.send(message);
    }

    public boolean checkDuplicatedEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    // void에서 PermissionRequestEntity로 변경
    public PermissionRequestEntity savePermissionEntity(PermissionRequestDTO dto) {
        Optional<MemberEntity> requesterEntity = memberRepository.findById(dto.getRequesterId()); // 요청보낸사람
        Optional<MemberEntity> inviteeEntity = memberRepository.findById(dto.getInviteeId()); // 초대받은사람
        Optional<ChildEntity> childEntity = childRepository.findById(dto.getChildId()); // 초대받은사람

        PermissionRequestEntity _entity = PermissionRequestEntity.builder()
                .requester(requesterEntity.get())
                .child(childEntity.get())
                .invitee(inviteeEntity.get())
                .relationType(dto.getRequesterRelationType())
                .permissionStatus(dto.getPermissionRequestStatus())
                .requestLinkCode(dto.getRequestCodeLink())
                .alias(dto.getAlias())
                .build();
        return permissionRequestRepository.save(_entity);
    }

    // 교사가 링크 클릭시 Child와 RelationShip생성.
    public RelationShipEntity saveRelationShipEntity(PermissionRequestEntity permissionRequestEntity) {
        Optional<MemberEntity> inviteeEntity = memberRepository.findById(permissionRequestEntity.getInvitee().getId()); // 초대받은사람
        Optional<ChildEntity> childEntity = childRepository.findById(permissionRequestEntity.getChild().getId());

        RelationShipEntity _entity = RelationShipEntity.builder()
                .member(inviteeEntity.get())
                .child(childEntity.get())
                .permissionLevel(PermissionLevel.VIEWER)
                .relationType(RelationType.TEACHER)
                .build();
        return relationShipRepository.save(_entity);
    }
    // 이메일 유효기간
    public boolean checkInTimeLimit(LocalDateTime time, long limitTime) {
        // 제한 시간 안이면 true, 제한시간 오버됐으면 false
        boolean result = false;
        if (Duration.between(time, LocalDateTime.now()).toMinutes() <= limitTime)
            result = true;
        else
            result = false;

        return result;

    }

    /**
     * 2025-03-10~12 이도훈
     * 부모용
     * @param memberId
     * @return
     */
    @Transactional
    public List<PermissionRequestDTO> findPermissionRequestDTOList(Long memberId) {

        List<PermissionRequestEntity> requesterEntity = permissionRequestRepository.findAllByRequesterId(memberId);

        //PermissionRequestDTO를 List에 담기 위해 변환 전 빈 List객체 생성.
        List<PermissionRequestDTO> dtoList = new ArrayList<>();
        //PermissionRequestEntity를 Dto로 변환 후 추가
        for (PermissionRequestEntity entity : requesterEntity) {
            dtoList.add(PermissionRequestDTO.builder()
                    .id(entity.getId())
                    .requesterId(entity.getRequester().getId())
                    .inviteeId(entity.getInvitee().getId())
                    .childId(entity.getChild().getId())
                    .childName(entity.getChild().getName())
                    .requesterRelationType(entity.getRequester().getRelationType())
                    .inviteeRelationType(entity.getRelationType())
                    .permissionRequestStatus(entity.getPermissionStatus())
                    .requestCodeLink(entity.getRequestLinkCode())
                    .alias(entity.getAlias())
                    .approvalDate(entity.getModifiedAt())
                    .build());
        }
        //Dto가 추가 된 List객체 반환
        return dtoList;
    }

    /**
     * 2025-03-10~12 정준성
     * 교사용
     * @param inviteeId
     * @param _waitRequestList
     * @param _permissionList
     */
    @Transactional
    public void findAllByPermissionEntity(
    		Long inviteeId,
    		List<PermissionTeacherDTO> _waitRequestList,
    		List<PermissionTeacherDTO> _permissionList)
    {
        // 25/3/11 jun : requester 기준으로 db 찾아서 반환하는 함수 교사용
        List<PermissionRequestEntity> list = permissionRequestRepository.findAllByInviteeId(inviteeId);

        for (var _entity : list)
        {
            // accept 상태라면 permissionList에 넣기 아니라면 pending
            if (_entity.getPermissionStatus().equals(PermissionRequestStatus.ACCEPTED) )
                _permissionList.add(entityToPermissionTeacherDTO(_entity));
            else
                _waitRequestList.add(entityToPermissionTeacherDTO(_entity));
        }
    }

    /**
     * 2025-03-10~12 정준성
     * 교사용
     * @param _entity
     * @return
     */
    @Transactional
    private PermissionTeacherDTO entityToPermissionTeacherDTO(PermissionRequestEntity _entity) {
        return PermissionTeacherDTO.builder()
                .id(_entity.getId())
                .guardianName(_entity.getRequester().getName())
                .childName(_entity.getChild().getName())
                .approvalDate(_entity.getModifiedAt())
                .birthDate(_entity.getChild().getBirthDate())
                .requesterRelationType(_entity.getRequester().getRelationType())
                .inviteeRelationType(_entity.getRelationType())
                .build();
    }

    // 나중에 옮겨야 할 함수
    // permission table의 record 삭제 함수
    /**
	 * 2025-03-10~12
	 * 삭제 메서드(부모, 교사 공용)
     * @param permissionId
     * @return
     */
    public boolean deletePermissionTable(Long permissionId)
    {
        boolean isPermission = permissionRequestRepository.existsById(permissionId);
        if(!isPermission) return false;

        Optional<PermissionRequestEntity> permissionEntity = permissionRequestRepository.findById(permissionId);
        if (permissionEntity.isPresent()) {
            // 삭제 전, 관련된 relationshipId를 변수로 저장할 수 있음 (예시)
            Long relationshipId = permissionEntity.get().getInvitee().getId();

            permissionRequestRepository.delete(permissionEntity.get());

            relationShipRepository.deleteById(relationshipId);

        }
        return true;
    }

    @Transactional
	public boolean cancelEmailInviteLink(
			Long permissionId,
			Long childId,
			String alias) {

    	Optional<ChildEntity> child = childRepository.findById(childId);

	    Optional<PermissionRequestEntity> permissionEntity = permissionRequestRepository
	            .findByidAndChildAndAlias(permissionId, child, alias);

	    if (!permissionEntity.isPresent()) {
	        return false; // 해당하는 요청이 없다면 삭제하지 않음
	    }

	    // Permission 요청 삭제
	    permissionRequestRepository.delete(permissionEntity.get());

	    return true; // 삭제 성공 시 true 반환
	}
}