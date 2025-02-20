package com.scit.iLog.domain.sentimentalAnalysis;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "weather")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeatherEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_target_id", nullable = false)
    private AnalysisTargetEntity analysisTarget;

    @Column(name = "temperature")
    private double temperature;

    @Column(name = "humidity")
    private int humidity;

    @Column(name = "wind_speed")
    private double windSpeed;

    @Column(name = "weather_desc")
    private String weatherDesc;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}

