package com.scit.iLog.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "child_media")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildMediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private ChildEntity child;

    @Column(name = "analysis_result")
    private String analysisResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ChildMediaType type;

}
