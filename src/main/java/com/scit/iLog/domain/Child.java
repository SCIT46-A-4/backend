package com.scit.iLog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "child")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Child extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    @Column(name = "weight")
    private double weight;

    @Column(name = "height")
    private double height;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Builder.Default
    @OneToMany(mappedBy = "child")
    private List<ChildDiary> diaries = new ArrayList<>();
}
