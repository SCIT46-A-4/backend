package com.scit.iLog;

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
import com.scit.iLog.domain.sentimentalAnalysis.*;
import com.scit.iLog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

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
    private final PasswordEncoder passwordEncoder;
    private final AnalysisSatisfactionRepository analysisSatisfactionRepository;

    @Override
    public void run(String... args) throws Exception {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDateTime now = LocalDateTime.now();

        // 예시용 배열 (랜덤 데이터 생성을 위한 샘플)
        String[] firstNames = {"김", "이", "박", "최", "정"};
        String[] lastNames = {"철수", "영희", "갑돌", "갑순", "지영"};
        String[] locations = {"서울", "부산", "인천", "대구", "광주"};
        EmotionType[] emotionTypes = EmotionType.values();

        // admin은 테스트용 관리자 계정 (한 번만 생성)
        MemberEntity admin = MemberEntity.builder()
                .name("ADMIN")
                .password(passwordEncoder.encode("ADMIN123!"))
                .signInId("ADMIN")
                .email("admin@example.com")
                .role(MemberRole.ADMIN)
                .personalInformationCollectionAndUsageAgreement(false)
                .build();
        memberRepository.save(admin);

        for (int i = 1; i <= 10; i++) {
            // 1. Member 엔티티 생성 (랜덤 이름, 이메일 등)
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String fullName = firstName + " " + lastName;
            String signInId = (firstName + "_" + lastName).toLowerCase() + "_" + random.nextInt(1000);
            String email = firstName.toLowerCase() + lastName.toLowerCase() + random.nextInt(100) + "@example.com";

            MemberEntity mom = MemberEntity.builder()
                    .name(fullName)
                    .password(passwordEncoder.encode("Password123!"))
                    .signInId(signInId)
                    .email(email)
                    .role(MemberRole.USER)
                    .relationType(RelationType.GUARDIAN)
                    .personalInformationCollectionAndUsageAgreement(true)
                    .build();
            memberRepository.save(mom);

            // 각 mom마다 2명의 자녀 생성
            for (int j = 0; j < 2; j++) {
                // 2. Child 엔티티 생성 (랜덤 이름, 생년월일, 출생지 등)
                String childName = "Baby " + firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
                String birthLocation = locations[random.nextInt(locations.length)];

                ChildEntity girl = ChildEntity.builder()
                        .name(childName)
                        .birthDate(now.minusYears(random.nextInt(3) + 1)) // 1~3세
                        .birthLocation(birthLocation)
                        .note("Healthy baby with energy " + random.nextInt(100))
                        .originalProfileImgName("profile_orig_" + random.nextInt(1000) + ".jpg")
                        .savedProfileImgName("profile_saved_" + random.nextInt(1000) + ".jpg")
                        .gender(Gender.WOMAN)
                        .build();
                childRepository.save(girl);

                // 12. Relationship 엔티티 생성 (연관관계 객체로 연결)
                RelationShipEntity relationship = RelationShipEntity.builder()
                        .child(girl)
                        .member(mom)
                        .permissionLevel(PermissionLevel.OWNER)
                        .relationType(RelationType.GUARDIAN)
                        .build();
                relationShipRepository.save(relationship);

                // 각 자녀마다 40개의 AnalysisTarget 및 연관 데이터 생성
                for (int k = 0; k < 40; k++) {
                    // 3. AnalysisTarget 엔티티 생성
                    AnalysisTargetEntity analysisTarget = AnalysisTargetEntity.builder()
                            .child(girl)
                            .registerDate(now.minusDays(random.nextInt(30)))
                            .uploadedBy(mom)
                            .originalTargetFileName("survey_orig_" + random.nextInt(1000) + ".jpg")
                            .savedTargetFileName("survey_saved_" + random.nextInt(1000) + ".jpg")
                            .supplement("Supplement info " + random.nextInt(100))
                            .companion("Companion info " + random.nextInt(100))
                            .type(AnalysisType.PHOTO)
                            .build();
                    analysisTargetRepository.save(analysisTarget);

                    // 13. Weather 엔티티 생성
                    WeatherEntity weather = WeatherEntity.builder()
                            .humidity(50 + random.nextInt(50))
                            .temperature(20.0 + random.nextDouble() * 15)
                            .windSpeed(3.0 + random.nextDouble() * 7)
                            .analysisTarget(analysisTarget)
                            .recordedAt(now.minusHours(random.nextInt(24)))
                            .weatherDesc("Weather: " + (random.nextBoolean() ? "Clear" : "Cloudy"))
                            .build();
                    // 연관 대상에 weather 설정 (setter 사용)
                    analysisTarget.setWeather(weather);

                    // 4. AnalysisResult 엔티티 생성
                    AnalysisResultEntity analysisResult = AnalysisResultEntity.builder()
                            .emotionScore(0.5 + random.nextDouble())
                            .analysisTarget(analysisTarget)
                            .analysisResult("Detailed analysis result " + random.nextInt(1000))
                            .suggestedSolution("Suggested solution " + random.nextInt(500))
                            .emotionType(emotionTypes[random.nextInt(emotionTypes.length)])
                            .build();
                    analysisResultRepository.save(analysisResult);

                    // 5. AnalysisResultNote 엔티티 생성
                    AnalysisResultNoteEntity analysisResultNote = AnalysisResultNoteEntity.builder()
                            .analysisResult(analysisResult)
                            .content("This is a note on the analysis result " + random.nextInt(1000))
                            .build();
                    AnalysisSatisfactionEntity analysisSatisfaction = AnalysisSatisfactionEntity.builder()
                            .satisfactionScore(1 + random.nextInt(10))
                            .analysisResult(analysisResult)
                            .build();
                    analysisResultNoteRepository.save(analysisResultNote);
                    analysisSatisfactionRepository.save(analysisSatisfaction);
                }
                // 6. ChildDiary 엔티티 생성 (40개)
                for (int k = 0; k < 40; k++) {
                    ChildDiaryEntity diary = ChildDiaryEntity.builder()
                            .author(mom)
                            .child(girl)
                            .content("Today, " + girl.getName() + " had a great day at preschool. " + random.nextInt(100))
                            .title("Daily Diary " + random.nextInt(1000))
                            .build();
                    childDiaryRepository.save(diary);
                }
                // 7. ChildRecord 엔티티 생성 (40개) 및 HealthCheck 생성
                for (int k = 0; k < 40; k++) {
                    ChildRecordEntity record = ChildRecordEntity.builder()
                            .child(girl)
                            .height(70.0 + random.nextDouble() * 20)  // 70 ~ 90
                            .leftEye(0.8 + random.nextDouble() * 0.4)   // 0.8 ~ 1.2
                            .rightEye(0.8 + random.nextDouble() * 0.4)  // 0.8 ~ 1.2
                            .weight(8.0 + random.nextDouble() * 10)     // 8 ~ 18
                            .registerDate(now.minusDays(random.nextInt(30)))
                            .note("Child record note " + random.nextInt(1000))
                            .build();
                    childRecordRepository.save(record);

                    // 11. HealthCheck 엔티티 생성
                    HealthCheckEntity healthCheck = HealthCheckEntity.builder()
                            .child(girl)
                            .childRecord(record)
                            .member(mom)
                            .originalFileName("health_orig_" + random.nextInt(1000) + ".jpg")
                            .savedFileName("health_saved_" + random.nextInt(1000) + ".jpg")
                            .build();
                    childHealthCheckRepository.save(healthCheck);
                }
            }
            // 8. Claim 엔티티 생성 (각 mom에 대해 한 건)
            ClaimEntity claim = ClaimEntity.builder()
                    .author(mom)
                    .title("Service Issue " + random.nextInt(1000))
                    .content("There is an issue with the service " + random.nextInt(1000))
                    .type(ClaimType.USAGE)
                    .build();
            claimRepository.save(claim);

            // 9. ClaimAnswer 엔티티 생성 (각 mom에 대해 한 건)
            ClaimAnswerEntity claimAnswer = ClaimAnswerEntity.builder()
                    .claim(claim)
                    .author(admin)
                    .title("Re: Service Issue " + random.nextInt(1000))
                    .content("We have resolved the issue " + random.nextInt(1000))
                    .build();
            claimAnswerRepository.save(claimAnswer);
        }

        // 10. Guide 엔티티 생성 (admin 기준, 10개)
        for (int i = 0; i < 10; i++) {
            GuideEntity guide = GuideEntity.builder()
                    .author(admin)
                    .title("User Guide " + random.nextInt(1000))
                    .content("This guide explains how to use the service " + random.nextInt(1000))
                    .build();
            guideRepository.save(guide);
        }

        System.out.println("테스트용 기본 엔티티 저장 완료");
    }
}

