package com.scit.iLog.domain.child;

import static jakarta.persistence.FetchType.LAZY;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    
    @Column(name="created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;  // createdAt 필드
}
