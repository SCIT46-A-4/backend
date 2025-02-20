package com.scit.iLog;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.scit.iLog.domain.GuideEntity;
import com.scit.iLog.domain.PermissionLevel;
import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.child.ChildDiaryEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.child.ChildRecordEntity;
import com.scit.iLog.domain.child.Gender;
import com.scit.iLog.domain.claim.ClaimAnswerEntity;
import com.scit.iLog.domain.claim.ClaimEntity;
import com.scit.iLog.domain.claim.ClaimType;
import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.member.MemberRole;
import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultEntity;
import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultNoteEntity;
import com.scit.iLog.domain.sentimentalAnalysis.AnalysisTargetEntity;
import com.scit.iLog.domain.sentimentalAnalysis.ChildAssetType;
import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import com.scit.iLog.domain.sentimentalAnalysis.WeatherEntity;
import com.scit.iLog.repository.AnalysisResultNoteRepository;
import com.scit.iLog.repository.AnalysisResultRepository;
import com.scit.iLog.repository.AnalysisTargetRepository;
import com.scit.iLog.repository.ChildDiaryRepository;
import com.scit.iLog.repository.ChildHealthCheckRepository;
import com.scit.iLog.repository.ChildRecordRepository;
import com.scit.iLog.repository.ChildRepository;
import com.scit.iLog.repository.ClaimAnswerRepository;
import com.scit.iLog.repository.ClaimRepository;
import com.scit.iLog.repository.GuideRepository;
import com.scit.iLog.repository.MemberRepository;
import com.scit.iLog.repository.RelationShipRepository;

import lombok.RequiredArgsConstructor;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final AnalysisTargetRepository analysisTargetRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisResultNoteRepository analysisResultNoteRepository;
    private final ChildDiaryRepository childDiaryRepository;
    private final ChildRecordRepository childRecordRepository;
    private final ClaimRepository claimRepository;
    private final ClaimAnswerRepository claimAnswerRepository;
    private final GuideRepository guideRepository;
    private final ChildHealthCheckRepository childHealthCheckRepository;
    private final RelationShipRepository relationShipRepository;

    @Override
    public void run(String... args) throws Exception {
        LocalDateTime now = LocalDateTime.now();

        MemberEntity admin = MemberEntity.builder()
                .name("Jane Doe")
                .password("Password1234!")
                .signInId("jane_doe")
                .email("jane@example.com")
                .role(MemberRole.ADMIN)
                .build();
        memberRepository.save(admin);

        for (int i = 1; i <= 10; i++) {
            // 1. Member 엔티티 생성
            MemberEntity mom = MemberEntity.builder()
                    .name("John Doe " + i)
                    .password("Password123!")
                    .signInId("john_doe_" + i)
                    .email("john" + i + "@example.com")
                    .role(MemberRole.USER)
                    .relationType(RelationType.GUARDIAN)
                    .build();
            memberRepository.save(mom);

            // 2. Child 엔티티 생성
            ChildEntity girl = ChildEntity.builder()
                    .name("Baby Jane " + i)
                    .birthDate(now.minusYears(1))
                    .birthLocation("Seoul")
                    .note("Healthy baby " + i)
                    .originalProfileImgName("profile_orig_" + i + ".jpg")
                    .savedProfileImgName("profile_saved_" + i + ".jpg")
                    .gender(Gender.WOMAN)
                    .build();
            childRepository.save(girl);

            // 12. Relationship 엔티티 생성 (Child와 Member 객체 참조)
            RelationShipEntity relationship = RelationShipEntity.builder()
                    .child(girl)
                    .member(mom)
                    .permissionLevel(PermissionLevel.OWNER)   // ENUM: 'EDITOR','OWNER','VIEWER'
                    .relationType(RelationType.GUARDIAN)         // ENUM: 'CARER','EXPERT','GUARDIAN','PARENT','TEACHER'
                    .build();
            relationShipRepository.save(relationship);

            // 3. AnalysisTarget 엔티티 생성 (child와 uploadedBy는 엔티티 객체로 설정)
            AnalysisTargetEntity analysisTarget = AnalysisTargetEntity.builder()
                    .child(girl)
                    .registerDate(now)
                    .uploadedBy(mom)
                    .originalSurveyFileName("survey_orig_" + i + ".jpg")
                    .savedSurveyFileName("survey_saved_" + i + ".jpg")
                    .supplement("Supplement info " + i)
                    .companion("Companion info " + i)
                    .type(ChildAssetType.PHOTO)
                    .build();
            analysisTargetRepository.save(analysisTarget);

            // 13. Weather 엔티티 생성 (AnalysisTarget 객체 참조)
            WeatherEntity weather = WeatherEntity.builder()
                    .humidity(65 + i)
                    .temperature(26.3f + i)
                    .windSpeed(5.8f + i)
                    .analysisTarget(analysisTarget) // 연관된 AnalysisTarget 객체
                    .recordedAt(now)
                    .weatherDesc("Clear and sunny " + i)
                    .build();
            // AnalysisTarget에 weather 설정 (setter가 있다고 가정)
            analysisTarget.setWeather(weather);

            // 4. AnalysisResult 엔티티 생성 (AnalysisTarget 객체 참조)
            AnalysisResultEntity analysisResult = AnalysisResultEntity.builder()
                    .emotionScore(0.85 + i * 0.01)
                    .analysisTarget(analysisTarget) // 연관관계를 객체로 연결
                    .analysisResult("Detailed analysis result " + i)
                    .suggestedSolution("Suggested solution " + i)
                    .emotionType(EmotionType.ANGRY) // ENUM: 'ANGRY','ANXIOUS','BORED','CONFUSED',...
                    .build();
            analysisResultRepository.save(analysisResult);

            // 5. AnalysisResultNote 엔티티 생성 (AnalysisResult 객체 참조)
            AnalysisResultNoteEntity analysisResultNote = AnalysisResultNoteEntity.builder()
                    .satisfactionLevel(5)
                    .analysisResult(analysisResult) // 연관관계 객체 참조
                    .content("This is a note on the analysis result " + i)
                    .build();
            analysisResultNoteRepository.save(analysisResultNote);

            // 6. ChildDiary 엔티티 생성 (author와 child를 객체로 연결)
            ChildDiaryEntity diary = ChildDiaryEntity.builder()
                    .author(mom) // Member 엔티티 객체
                    .child(girl) // Child 엔티티 객체
                    .content("Today, the child had a great day at preschool. " + i)
                    .title("Daily Diary " + i)
                    .build();
            childDiaryRepository.save(diary);

            // 7. ChildRecord 엔티티 생성 (Child 객체 참조)
            ChildRecordEntity record = ChildRecordEntity.builder()
                    .child(girl) // Child 객체
                    .height(75.5 + i)
                    .leftEye(1.0)
                    .rightEye(1.1)
                    .weight(10.2 + i)
                    .registerDate(now)
                    .note("Child record note " + i)
                    .build();
            childRecordRepository.save(record);

            // 8. Claim 엔티티 생성 (Member 객체 참조)
            ClaimEntity claim = ClaimEntity.builder()
                    .author(mom) // Member 객체
                    .title("Service Issue " + i)
                    .content("There is an issue with the service " + i)
                    .type(ClaimType.USAGE) // ENUM: 'GENERAL','PRIVACY','USAGE'
                    .build();
            claimRepository.save(claim);

            // 9. ClaimAnswer 엔티티 생성 (Claim과 Member 객체 참조)
            ClaimAnswerEntity claimAnswer = ClaimAnswerEntity.builder()
                    .claim(claim)   // Claim 객체
                    .author(admin)  // 미리 생성한 admin 객체 사용
                    .title("Re: Service Issue " + i)
                    .content("We have resolved the issue " + i)
                    .build();
            claimAnswerRepository.save(claimAnswer);

            // 10. Guide 엔티티 생성 (Member 객체 참조)
            GuideEntity guide = GuideEntity.builder()
                    .author(admin)
                    .title("User Guide " + i)
                    .content("This guide explains how to use the service " + i)
                    .build();
            guideRepository.save(guide);

            // 11. HealthCheck 엔티티 생성 (Child, ChildRecord, Member 객체 참조)
            HealthCheckEntity healthCheck = HealthCheckEntity.builder()
                    .child(girl)
                    .childRecord(record)
                    .member(mom)
                    .originalFileName("health_orig_" + i + ".jpg")
                    .savedFileName("health_saved_" + i + ".jpg")
                    .build();
            childHealthCheckRepository.save(healthCheck);
        }

        System.out.println("테스트용 기본 엔티티 저장 완료");
    }
}

