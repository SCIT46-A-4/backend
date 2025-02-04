package com.scit.iLog.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "qna")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QAndA extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @Column(name = "title")
    private String title;

    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;

    @Enumerated(EnumType.STRING)
    private QuestionCategory category;
}
