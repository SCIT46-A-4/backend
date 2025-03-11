package com.scit.iLog;

import com.scit.iLog.domain.GuideEntity;
import com.scit.iLog.domain.PermissionLevel;
import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.child.*;
import com.scit.iLog.domain.claim.ClaimAnswerEntity;
import com.scit.iLog.domain.claim.ClaimEntity;
import com.scit.iLog.domain.claim.ClaimType;
import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.member.MemberRole;
import com.scit.iLog.domain.mentalsurvey.MentalSurveyResponseEntity;
import com.scit.iLog.domain.mentalsurvey.QuestionResponse;
import com.scit.iLog.domain.mentalsurvey.SectionResponse;
import com.scit.iLog.domain.sentimentalAnalysis.*;
import com.scit.iLog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    private final FamilyBackgroundRepository familyBackgroundRepository; // 2025-02-28 / 김은진
    private final MentalSurveyResponseRepository mentalSurveyResponseRepository;
    private final AnalysisTypeRepository analysisTypeRepository;
    private final AnalysisTargetTypeRepository analysisTargetTypeRepository;
    private final ChildBackGroundRepository childBackGroundRepository;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDateTime now = LocalDateTime.now();

        // 예시용 배열 (랜덤 데이터 생성을 위한 샘플)
        String[] firstNames = {"김", "이", "박", "최", "정"};
        String[] lastNames = {"철수", "영희", "갑돌", "갑순", "지영"};
        String[] locations = {"서울", "부산", "인천", "대구", "광주"};
        EmotionType[] emotionTypes = EmotionType.values();
        AnalysisType[] analysisTypes = AnalysisType.values();

        // admin은 테스트용 관리자 계정 (한 번만 생성)
        MemberEntity admin = MemberEntity.builder()
                .name("ADMIN")
                .signInId("ADMIN")
                .password(passwordEncoder.encode("ADMIN123!"))
                .email("admin@example.com")
                .role(MemberRole.ADMIN)
                .relationType(RelationType.ADMIN)
                .personalInformationCollectionAndUsageAgreement(false)
                .build();
        memberRepository.save(admin);

        MemberEntity player = MemberEntity.builder()
                .name("player")
                .password(passwordEncoder.encode("asd123!"))
                .signInId("asd123")
                .email("asd@example.com")
                .role(MemberRole.USER)
                .relationType(RelationType.GUARDIAN)
                .personalInformationCollectionAndUsageAgreement(false)
                .build();
        memberRepository.save(player);

        MemberEntity dad = MemberEntity.builder()
                .name("daddy")
                .password(passwordEncoder.encode("dad123!"))
                .signInId("daddy")
                .email("dad@example.com")
                .role(MemberRole.USER)
                .personalInformationCollectionAndUsageAgreement(true)
                .relationType(RelationType.GUARDIAN)
                .build();
        memberRepository.save(dad);

        // 2025-02-28 / 김은진 / FamilyBackGround 초기 데이터 생성
        for (FamilyBackGround familyBackGround : FamilyBackGround.values()) {
            FamilyBackGroundEntity entity = new FamilyBackGroundEntity(familyBackGround);
            familyBackgroundRepository.save(entity);
        }

        for (AnalysisType analysisType : AnalysisType.values()) {
            AnalysisTypeEntity analysisTypeEntity = AnalysisTypeEntity.create(analysisType);
            analysisTypeRepository.save(analysisTypeEntity);
        }

        for (int i = 1; i <= 2; i++) {
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
            System.out.println(fullName + ": SignInId: " + signInId);

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
                        .originalProfileImgName(null)
                        .savedProfileImgName(null)
                        .gender(Gender.WOMAN)
                        .callName("혈연")
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

                // MentalSurveyResponseEntity 테스트 데이터 추가
// 각 자녀에 대해, 현재 실행일(now)부터 한 달 전까지 하루씩 응답 데이터를 생성
                LocalDateTime startDate = now.minusDays(30);
                for (LocalDateTime date = startDate; date.isBefore(now.plusDays(1)); date = date.plusDays(1)) {
                    List<SectionResponse> sectionResponses = new ArrayList<>();

                    // 섹션 1: "감정 표현 및 조절"
                    List<QuestionResponse> questionResponses1 = new ArrayList<>();
                    questionResponses1.add(new QuestionResponse("기본 감정 표현", "아이가 기쁠 때 웃음", random.nextInt(1, 6)));
                    questionResponses1.add(new QuestionResponse("비언어적 표현", "아이가 손짓, 표정으로 감정을 표현함", random.nextInt(1, 6)));
                    questionResponses1.add(new QuestionResponse("감정 조절", "화가 났을 때 빨리 진정됨", random.nextInt(1, 6)));
                    questionResponses1.add(new QuestionResponse("감정 변화", "최근 감정 변화가 뚜렷함", random.nextInt(1, 6)));
                    int section1Score = questionResponses1.stream().mapToInt(q -> q.getScore()).sum();
                    sectionResponses.add(new SectionResponse("감정 표현 및 조절", questionResponses1, section1Score));

                    // 섹션 2: "애착 행동 및 사회성"
                    List<QuestionResponse> questionResponses2 = new ArrayList<>();
                    questionResponses2.add(new QuestionResponse("애착 행동", "보호자에게 지나치게 매달림", random.nextInt(1, 6)));
                    questionResponses2.add(new QuestionResponse("낯선 사람 반응", "낯선 사람에게 경계심을 보임", random.nextInt(1, 6)));
                    questionResponses2.add(new QuestionResponse("놀이 행동", "특정 놀이에 집착하는 경향이 있음", random.nextInt(1, 6)));
                    questionResponses2.add(new QuestionResponse("또래 반응", "또래와의 상호작용 시 거부 반응을 보임", random.nextInt(1, 6)));
                    int section2Score = questionResponses2.stream().mapToInt(q -> q.getScore()).sum();
                    sectionResponses.add(new SectionResponse("애착 행동 및 사회성", questionResponses2, section2Score));

                    int totalScore = sectionResponses.stream().mapToInt(SectionResponse::getSectionLikertScore).sum();
                    String comment = "테스트 데이터: 심리 평가 결과 총점 " + totalScore;

                    MentalSurveyResponseEntity mentalSurveyResponse = MentalSurveyResponseEntity.builder()
                            .surveyId("survey1")
                            .surveyTitle("만 2~3세 심리 평가 체크리스트 - 버전 1")
                            .relationType(RelationType.GUARDIAN.name())
                            .childId(girl.getId())
                            .respondentId(mom.getId())
                            .totalLikertScore(totalScore)
                            .sectionResponses(sectionResponses)
                            .comment(comment)
                            .createdAt(date)
                            .lastModifiedAt(date.plusHours(1)).build(); // 예시로 생성 시각보다 1시간 후 수정시간 설정

                    mentalSurveyResponseRepository.save(mentalSurveyResponse);
                }

                // 각 자녀마다 40개의 AnalysisTarget 및 연관 데이터 생성
                for (int k = 0; k < 20; k++) {
                    // 3. AnalysisTarget 엔티티 생성
                    AnalysisTargetEntity analysisTarget = AnalysisTargetEntity.builder()
                            .child(girl)
                            .registerDate(now.minusDays(random.nextInt(30)))
                            .uploadedBy(mom)
                            .originalTargetFileName("test-target.png")
                            .savedTargetFileName("test-target.png")
                            .supplement("Supplement info " + random.nextInt(100))
                            .companion("Companion info " + random.nextInt(100))
                            .build();
                    analysisTargetRepository.save(analysisTarget);

                    List<AnalysisTargetTypeEntity> analysisTargetTypes = analysisTypeRepository.findAll()
                            .stream()
                            .map(analysisType -> AnalysisTargetTypeEntity.builder()
                                    .analysisTarget(analysisTarget)
                                    .analysisType(analysisType)
                                    .build())
                            .toList();
                    analysisTargetTypeRepository.saveAll(analysisTargetTypes);

                    // 13. Weather 엔티티 생성
                    WeatherEntity weather = WeatherEntity.builder()
                            .humidity(50 + random.nextInt(50))
                            .temperature(20.0 + random.nextDouble() * 15)
                            .windSpeed(3.0 + random.nextDouble() * 7)
                            .analysisTarget(analysisTarget)
                            .recordedAt(now.minusHours(random.nextInt(24)))
                            .weatherDesc("Weather: " + (random.nextBoolean() ? "Clear" : "Cloudy"))
                            .build();
                    analysisTarget.setWeather(weather);


                    // 4. AnalysisResult 엔티티 생성
                    AnalysisResultEntity analysisResult = AnalysisResultEntity.builder()
                            .emotionScore(random.nextDouble())
                            .analysisTarget(analysisTarget)
                            .title("Analysis " + UUID.randomUUID().toString())
                            .analysisResultText("Detailed analysis result " + random.nextInt(1000))
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
                for (int k = 0; k < 20; k++) {
                    ChildDiaryEntity diary = ChildDiaryEntity.builder()
                            .author(mom)
                            .child(girl)
                            .content("Today, " + girl.getName() + " had a great day at preschool. " + random.nextInt(100))
                            .title("Daily Diary " + random.nextInt(1000))
                            .build();
                    childDiaryRepository.save(diary);
                }
                // 7. ChildRecord 엔티티 생성 (40개) 및 HealthCheck 생성
                for (int k = 0; k < 20; k++) {
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

                    HealthCheckEntity healthCheck = HealthCheckEntity.builder()
                            .child(girl)
                            .childRecord(record)
                            .member(mom)
                            .originalFileName("test-healthCheck.png")
                            .savedFileName("test-healthCheck.png")
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

        // 2025-03-04 / 김은진 / 교사 계정 추가
        MemberEntity teacher = MemberEntity.builder()
                .signInId("teacher1")
                .password(passwordEncoder.encode("Teacher1!"))
                .name("김선생")
                .email("teacher@test.com")
                .relationType(RelationType.TEACHER)
                .personalInformationCollectionAndUsageAgreement(true)
                .build();
        memberRepository.save(teacher);

        // 교사용 테스트 아동 추가
        ChildEntity teacherChild = ChildEntity.builder()
                .name("김승현")
                .birthDate(LocalDateTime.of(2020, 1, 15, 0, 0))
                .gender(Gender.MAN)
                .birthLocation("평택")
                .note("언어치료 받고 있음")
                .savedProfileImgName(ChildEntity.DEFAULT_PROFILE_IMG)
                .build();
        childRepository.save(teacherChild);

        // 교사와 아동 관계 설정
        RelationShipEntity teacherChildRelation = RelationShipEntity.builder()
                .member(teacher)
                .child(teacherChild)
                .permissionLevel(PermissionLevel.VIEWER)
                .relationType(RelationType.TEACHER)
                .build();
        relationShipRepository.save(teacherChildRelation);

                        /*
                          2025-03-04~07 정준성(감정설문 데이터)
                          CreateMentalSurveyResponses 메서드 호출 (정신 설문 응답 데이터 생성)
                          몽고DB는 자동 삭제가 안되기 때문에 최초 실행 후 [주석처리] 필요
                        */
        //CreateMentalSurveyResponses();

                        /*
                        2025-03-04~07 이도훈(가정환경 데이터)
                        createFamilyBackgrounds 메서드 호출 (가정환경 enum타입의 데이터 생성)
                        util패키지의 FamilyBackGroundSerializer클래스 생성함.
                       */
        CreateFamilyBackgrounds();

    }

    private void CreateMentalSurveyResponses() {
        List<MentalSurveyResponseEntity> responses = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int j = 0; j <= 20; j++) { // 20번 반복
            for (int i = 1; i <= 20; i++) { // 1~20까지 반복
                MentalSurveyResponseEntity response = MentalSurveyResponseEntity.builder()
                        .surveyTitle(getRandomSurveyTitle())
                        .surveyId("survey_" + String.format("%03d", i + (j * 20)))
                        .childId((long) i) // 1~20번을 균등하게 배치
                        .respondentId((long) (i + j * 20)) // 유니크한 값
                        .relationType(getRandomRelationType())
                        .sectionResponses(List.of()) // 빈 리스트
                        .totalLikertScore(random.nextInt(100) + 1) // 1~100 랜덤 점수
                        .comment(getCommentBasedOnScore(i))
                        .createdAt(LocalDateTime.now().minusDays(20 - j)) // 20일 전부터 하루씩 증가
                        .lastModifiedAt(LocalDateTime.now().minusDays(19 - i)) // 하루 차이
                        .build();

                responses.add(response);
            }
        }

        mentalSurveyResponseRepository.saveAll(responses);
    }

    private String getRandomSurveyTitle() {
        String[] titles =
                {"우울증 테스트", "불안 테스트", "스트레스 테스트"};
        return titles[ThreadLocalRandom.current().nextInt(titles.length)];
    }

    private String getRandomRelationType() {
        String[] relations =
                {"부모", "교사", "상담사"};
        return relations[ThreadLocalRandom.current().nextInt(relations.length)];
    }

    private String getCommentBasedOnScore(int score) {
        if (score < 20)
            return "거의 문제가 없습니다.";
        if (score < 50)
            return "약간의 문제가 있습니다.";
        if (score < 80)
            return "주의가 필요합니다.";
        return "심각한 수준의 문제가 있습니다.";
    }

    // 이 메서드를 DataInitializer의 run 메서드 끝부분에 추가하여 호출하세요.
    private void CreateFamilyBackgrounds() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // childId 1~20 까지 각 아동별로 1~3개의 랜덤 가정환경을 생성
        for (long childId = 1L; childId <= 20L; childId++) {
            // 현재 아동 가져오기
            ChildEntity child = childRepository.findById(childId).orElse(null);
            if (child == null) {
                System.out.println("아동 id " + childId + "가 없습니다.");
                continue;
            }

            // 중복 방지를 위해 Enum을 리스트로 만들어 셔플
            List<FamilyBackGround> backgrounds = new ArrayList<>(Arrays.asList(FamilyBackGround.values()));
            Collections.shuffle(backgrounds);

            // 1~3개의 랜덤한 가정환경을 선택
            int backgroundCount = random.nextInt(1, 4);
            List<FamilyBackGround> selectedBackgrounds = backgrounds.subList(0, backgroundCount);

            // 선택한 가정환경을 DB에 저장
            for (FamilyBackGround background : selectedBackgrounds) {
                FamilyBackGroundEntity backgroundEntity = FamilyBackGroundEntity.builder()
                        .familyBackGround(background)
                        .build();
                backgroundEntity.setFamilyBackGround(background);
                familyBackgroundRepository.save(backgroundEntity);

                // 아동과 가정환경 간 연결 관계 생성
                ChildBackGroundEntity childBackGround = ChildBackGroundEntity.builder()
                        .child(child)
                        .familyBackGround(backgroundEntity)
                        .build();

                childBackGroundRepository.save(childBackGround);
            }
        }

        System.out.println("✅ 아동별 가정환경 데이터 초기화 완료");
    }


}
