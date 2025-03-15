package com.scit.iLog.domain.claim;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "claim")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity author;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ClaimType type;

    @OneToMany(mappedBy = "claim", fetch = LAZY, cascade = CascadeType.REMOVE)
    private List<ClaimAnswerEntity> answers;

    public void addAnswer(ClaimAnswerEntity answer) {
        this.answers.add(answer);
        answer.setClaim(this);
    }
}
