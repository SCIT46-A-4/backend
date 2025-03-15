package com.scit.iLog;

import com.scit.iLog.domain.*;
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
import com.scit.iLog.exception.FamilyBackgroundNotFoundException;
import com.scit.iLog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.scit.iLog.config.WebConfig.CHILD_PROFILE_REQUEST_ROOT_PATH;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
@Profile("dev")
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
    private final FamilyBackgroundRepository familyBackgroundRepository;
    private final MentalSurveyResponseRepository mentalSurveyResponseRepository;
    private final AnalysisTypeRepository analysisTypeRepository;
    private final AnalysisTargetTypeRepository analysisTargetTypeRepository;
    private final ChildBackGroundRepository childBackGroundRepository;
    private String testParentSignInId;
    private String testTeacherSignInId;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDateTime now = LocalDateTime.now();

        // 초기 회원 생성
        MemberEntity admin = createAdminAccount();

        // FamilyBackGround와 AnalysisType 초기 데이터 생성
        initializeFamilyBackgrounds();
        initializeAnalysisTypes();

        // 일반 회원(엄마) 및 자녀, 그리고 설문/분석/기록 데이터 생성
        for (int i = 1; i <= 2; i++) {
            MemberEntity mom = createMomAccount(random);
            createChildrenForMom(mom, random, now);
        }

        // 추가: 교사 계정 및 교사용 자녀, 관계 생성
        initializeTeacherAccounts(random, now);

        System.out.println("테스트용 기본 엔티티 저장 완료");
        System.out.println(String.format("테스트용 부모 아이디: %s", this.testParentSignInId));
        System.out.println(String.format("테스트용 교사 아이디: %s", this.testTeacherSignInId));
    }

    private MemberEntity createAdminAccount() {
        MemberEntity admin = MemberEntity.builder()
                .name("ADMIN")
                .signInId("ADMIN")
                .password(passwordEncoder.encode("ADMIN123!"))
                .email("admin@example.com")
                .role(MemberRole.ADMIN)
                .relationType(RelationType.ADMIN)
                .personalInformationCollectionAndUsageAgreement(false)
                .build();
        return memberRepository.save(admin);
    }

    private MemberEntity createMomAccount(ThreadLocalRandom random) {
        String[] firstNames = {"김", "이", "박", "최", "정"};
        String[] lastNames = {"철수", "영희", "갑돌", "갑순", "지영"};
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
        this.testParentSignInId = signInId;
        return memberRepository.save(mom);
    }

    private void createChildrenForMom(MemberEntity mom, ThreadLocalRandom random, LocalDateTime now) {
        String[] locations = {"서울", "부산", "인천", "대구", "광주"};
        // 각 mom마다 4명의 자녀 생성
        for (int j = 0; j < 4; j++) {
            String[] firstNames = {"김", "이", "박", "최", "정"};
            String[] lastNames = {"철수", "영희", "갑돌", "갑순", "지영"};
            String childName = "Baby " + firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
            String birthLocation = locations[random.nextInt(locations.length)];

            ChildEntity child = ChildEntity.builder()
                    .name(childName)
                    .birthDate(now.minusYears(random.nextInt(3) + 1))
                    .birthLocation(birthLocation)
                    .note("Healthy baby with energy " + random.nextInt(100))
                    .originalProfileImgName(null)
                    .savedProfileImgName(null)
                    .gender(Gender.WOMAN)
                    .callName("혈연")
                    .build();
            childRepository.save(child);

            // Relationship 생성
            RelationShipEntity relationship = RelationShipEntity.builder()
                    .child(child)
                    .member(mom)
                    .permissionLevel(PermissionLevel.OWNER)
                    .relationType(RelationType.GUARDIAN)
                    .build();
            relationShipRepository.save(relationship);

            // MentalSurveyResponse 테스트 데이터 생성
            createMentalSurveyResponses(child, mom, random, now);

            // AnalysisTarget 및 관련 데이터 생성
            createAnalysisTargets(child, mom, random, now);

            // ChildDiary, ChildRecord, HealthCheck 생성
            createChildDiaryEntities(child, mom, random, now);
            createChildRecordAndHealthCheck(child, mom, random, now);
        }
    }

    private void createMentalSurveyResponses(ChildEntity child, MemberEntity respondent, ThreadLocalRandom random, LocalDateTime now) {
        LocalDateTime startDate = now.minusDays(30);
        for (LocalDateTime date = startDate; date.isBefore(now.plusDays(1)); date = date.plusDays(1)) {
            List<SectionResponse> sectionResponses = new ArrayList<>();

            // 섹션 1: 감정 표현 및 조절
            List<QuestionResponse> qResponses1 = Arrays.asList(
                    new QuestionResponse("기본 감정 표현", "아이가 기쁠 때 웃음", random.nextInt(1, 6)),
                    new QuestionResponse("비언어적 표현", "아이가 손짓, 표정으로 감정을 표현함", random.nextInt(1, 6)),
                    new QuestionResponse("감정 조절", "화가 났을 때 빨리 진정됨", random.nextInt(1, 6)),
                    new QuestionResponse("감정 변화", "최근 감정 변화가 뚜렷함", random.nextInt(1, 6))
            );
            int section1Score = qResponses1.stream().mapToInt(QuestionResponse::getScore).sum();
            sectionResponses.add(new SectionResponse("감정 표현 및 조절", qResponses1, section1Score));

            // 섹션 2: 애착 행동 및 사회성
            List<QuestionResponse> qResponses2 = Arrays.asList(
                    new QuestionResponse("애착 행동", "보호자에게 지나치게 매달림", random.nextInt(1, 6)),
                    new QuestionResponse("낯선 사람 반응", "낯선 사람에게 경계심을 보임", random.nextInt(1, 6)),
                    new QuestionResponse("놀이 행동", "특정 놀이에 집착하는 경향이 있음", random.nextInt(1, 6)),
                    new QuestionResponse("또래 반응", "또래와의 상호작용 시 거부 반응을 보임", random.nextInt(1, 6))
            );
            int section2Score = qResponses2.stream().mapToInt(QuestionResponse::getScore).sum();
            sectionResponses.add(new SectionResponse("애착 행동 및 사회성", qResponses2, section2Score));

            int totalScore = sectionResponses.stream().mapToInt(SectionResponse::getSectionLikertScore).sum();
            String comment = "테스트 데이터: 심리 평가 결과 총점 " + totalScore;

            MentalSurveyResponseEntity response = MentalSurveyResponseEntity.builder()
                    .surveyId("survey1")
                    .surveyTitle("만 2~3세 심리 평가 체크리스트 - 버전 1")
                    .relationType(RelationType.GUARDIAN.name())
                    .childId(child.getId())
                    .respondentId(respondent.getId())
                    .totalLikertScore(totalScore)
                    .sectionResponses(sectionResponses)
                    .comment(comment)
                    .createdAt(date)
                    .lastModifiedAt(date.plusHours(1))
                    .build();

            mentalSurveyResponseRepository.save(response);
        }
    }

    private void createAnalysisTargets(ChildEntity child, MemberEntity uploader, ThreadLocalRandom random, LocalDateTime now) {
        EmotionType[] emotionTypes = EmotionType.values();
        List<AnalysisType> analysisTypeList = new ArrayList<>(Arrays.asList(AnalysisType.values()));
        for (int k = 0; k < 20; k++) {
            AnalysisTargetEntity target = AnalysisTargetEntity.builder()
                    .child(child)
                    .registerDate(now.minusDays(random.nextInt(30)))
                    .uploadedBy(uploader)
                    .originalTargetFileName("test-target.png")
                    .savedTargetFileName("test-target.png")
                    .supplement("Supplement info " + random.nextInt(100))
                    .companion("Companion info " + random.nextInt(100))
                    .build();
            analysisTargetRepository.save(target);

            Collections.shuffle(analysisTypeList);
            List<AnalysisType> selectedTypes = analysisTypeList.subList(0, 3);
            List<AnalysisTargetTypeEntity> targetTypes = analysisTypeRepository.findAll().stream()
                    .filter(at -> selectedTypes.contains(at.getType()))
                    .map(at -> AnalysisTargetTypeEntity.builder()
                            .analysisTarget(target)
                            .analysisType(at)
                            .build())
                    .collect(Collectors.toList());
            analysisTargetTypeRepository.saveAll(targetTypes);

            WeatherEntity weather = WeatherEntity.builder()
                    .humidity(50 + random.nextInt(50))
                    .temperature(20.0 + random.nextDouble() * 15)
                    .windSpeed(3.0 + random.nextDouble() * 7)
                    .analysisTarget(target)
                    .recordedAt(now.minusHours(random.nextInt(24)))
                    .weatherDesc("Weather: " + (random.nextBoolean() ? "Clear" : "Cloudy"))
                    .build();
            target.setWeather(weather);

            AnalysisResultEntity analysisResult = AnalysisResultEntity.builder()
                    .emotionScore(random.nextDouble())
                    .analysisTarget(target)
                    .title("Analysis " + UUID.randomUUID().toString())
                    .analysisResultText("Detailed analysis result " + random.nextInt(1000))
                    .suggestedSolution("Suggested solution " + random.nextInt(500))
                    .emotionType(emotionTypes[random.nextInt(emotionTypes.length)])
                    .build();
            analysisResultRepository.save(analysisResult);

            AnalysisResultNoteEntity note = AnalysisResultNoteEntity.builder()
                    .analysisResult(analysisResult)
                    .content("This is a note on the analysis result " + random.nextInt(1000))
                    .build();
            AnalysisSatisfactionEntity satisfaction = AnalysisSatisfactionEntity.builder()
                    .satisfactionScore(random.nextInt(5))
                    .analysisResult(analysisResult)
                    .build();
            analysisResultNoteRepository.save(note);
            analysisSatisfactionRepository.save(satisfaction);
        }
    }

    private void createChildDiaryEntities(ChildEntity child, MemberEntity author, ThreadLocalRandom random, LocalDateTime now) {
        for (int k = 0; k < 20; k++) {
            ChildDiaryEntity diary = ChildDiaryEntity.builder()
                    .author(author)
                    .child(child)
                    .content("Today, " + child.getName() + " had a great day at preschool. " + random.nextInt(100))
                    .title("Daily Diary " + random.nextInt(1000))
                    .build();
            childDiaryRepository.save(diary);
        }
    }

    private void createChildRecordAndHealthCheck(ChildEntity child, MemberEntity author, ThreadLocalRandom random, LocalDateTime now) {
        for (int k = 0; k < 20; k++) {
            ChildRecordEntity record = ChildRecordEntity.builder()
                    .child(child)
                    .height(70.0 + random.nextDouble() * 20)
                    .leftEye(0.8 + random.nextDouble() * 0.4)
                    .rightEye(0.8 + random.nextDouble() * 0.4)
                    .weight(8.0 + random.nextDouble() * 10)
                    .registerDate(now.minusDays(random.nextInt(30)))
                    .note("Child record note " + random.nextInt(1000))
                    .build();
            childRecordRepository.save(record);

            HealthCheckEntity healthCheck = HealthCheckEntity.builder()
                    .child(child)
                    .childRecord(record)
                    .member(author)
                    .originalFileName("test-healthCheck.png")
                    .savedFileName("test-healthCheck.png")
                    .build();
            childHealthCheckRepository.save(healthCheck);
        }
    }

    private void initializeClaims(ThreadLocalRandom random, LocalDateTime now) {
        // 각 mom 당 한 건의 Claim 및 ClaimAnswer 생성
        // 이 메서드는 필요에 따라 별도로 분리할 수 있습니다.
    }

    private void initializeGuideEntities(ThreadLocalRandom random, LocalDateTime now) {
        for (int i = 0; i < 10; i++) {
            GuideEntity guide = GuideEntity.builder()
                    .author(memberRepository.findById(1L).orElse(null))
                    .title("User Guide " + random.nextInt(1000))
                    .content("This guide explains how to use the service " + random.nextInt(1000))
                    .build();
            guideRepository.save(guide);
        }
    }

    private void initializeTeacherAccounts(ThreadLocalRandom random, LocalDateTime now) {
        // 교사 계정 생성
        MemberEntity teacher = MemberEntity.builder()
                .signInId("teacher1")
                .password(passwordEncoder.encode("Teacher1!"))
                .name("김선생")
                .email("teacher@test.com")
                .relationType(RelationType.TEACHER)
                .personalInformationCollectionAndUsageAgreement(true)
                .build();
        memberRepository.save(teacher);
        this.testTeacherSignInId = "teacher1";

        // 기존에 저장된 아동 중 하나를 선택하여 교사와 관계 생성
        List<ChildEntity> existingChildren = childRepository.findAll();
        if (!existingChildren.isEmpty()) {
            // 예시로 첫 번째 아동을 선택 (필요에 따라 다른 조건을 적용할 수 있음)
            ChildEntity existingChild = existingChildren.get(0);
            RelationShipEntity teacherChildRelation = RelationShipEntity.builder()
                    .member(teacher)
                    .child(existingChild)
                    .permissionLevel(PermissionLevel.VIEWER)
                    .relationType(RelationType.TEACHER)
                    .build();
            relationShipRepository.save(teacherChildRelation);
        } else {
            log.warn("기존에 저장된 아동이 없습니다. 교사와의 관계를 생성할 수 없습니다.");
        }
    }

    private void initializeFamilyBackgrounds() {
        // FamilyBackGround 초기 데이터 생성
        for (FamilyBackGround familyBackGround : FamilyBackGround.values()) {
            FamilyBackGroundEntity entity = new FamilyBackGroundEntity(familyBackGround);
            familyBackgroundRepository.save(entity);
        }
        System.out.println("✅ 아동별 가정환경 데이터 초기화 완료");
    }

    private void initializeAnalysisTypes() {
        for (AnalysisType analysisType : AnalysisType.values()) {
            AnalysisTypeEntity entity = AnalysisTypeEntity.create(analysisType);
            analysisTypeRepository.save(entity);
        }
    }
}