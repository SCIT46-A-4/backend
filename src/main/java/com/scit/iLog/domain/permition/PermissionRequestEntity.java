package com.scit.iLog.domain.permition;

import java.time.LocalDateTime;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "permission_request")
@Builder
@Getter
public class PermissionRequestEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 권한요청을 “누가(보내는 사람)” 했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private MemberEntity requester;

    // 권한요청을 “누가(받는 사람)” 수락/거절할 것인지
    // 이미 가입된 사용자이면 MemberEntity 참조,
    // 미가입자이면 이메일/전화번호만 저장하는 식으로도 설계 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id")
    private MemberEntity invitee;

    // 어떤 아동에 대한 권한인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private ChildEntity child;

    // 요청된 관계유형(교사, 보호자, 기타 ...)
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_relation_type", nullable = false)
    private RelationType relationType;

    // PENDING, ACCEPTED, DENIED, EXPIRED 등
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PermissionRequestStatus permissionStatus;

    // 기타 필요하다면 초대코드, 만료일시, 메시지 등
    @Column(name = "request_link_code")
    private String requestLinkCode;
    
    // 그럼 저기에 있는 사람의 별칭은 어떻게 세팅할 것인가?
    @Column(name = "alias")
    private String alias;
    
    public void setPermissionStatusAndDeleteRequestLinkCode(PermissionRequestStatus status)
    {
    	// 스태이터스 변경 로직, 변경이 되는 순간 코드는 초기화한다.
    	this.permissionStatus = status;
    	this.requestLinkCode = null;
    }
    
    public void setRequestLinkCode(String str) {requestLinkCode = str;}
}