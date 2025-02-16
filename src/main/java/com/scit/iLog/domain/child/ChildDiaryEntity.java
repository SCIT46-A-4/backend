package com.scit.iLog.domain.child;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "child_diary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildDiaryEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildEntity child;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private MemberEntity author;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
