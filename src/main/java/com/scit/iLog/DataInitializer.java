package com.scit.iLog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scit.iLog.domain.*;
import com.scit.iLog.domain.child.*;
import com.scit.iLog.domain.claim.ClaimAnswerEntity;
import com.scit.iLog.domain.claim.ClaimEntity;
import com.scit.iLog.domain.claim.ClaimType;
import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.member.MemberRole;
import com.scit.iLog.domain.mentalsurvey.*;
import com.scit.iLog.domain.sentimentalAnalysis.*;
import com.scit.iLog.exception.FamilyBackgroundNotFoundException;
import com.scit.iLog.repository.*;
import com.scit.iLog.util.AgeCalculator;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
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
    // 보충 설명 예시 (보호자가 관찰 시 제공하는 추가 정보)
    // 예: 관찰 시각, 장소, 식사 여부, 환경 등 자세한 내용을 기술
    private static String[] supplementaryComments = {
            "오후 3시 30분, 집 근처 공원에서 아이가 뛰어놀며 간식을 먹은 후 활발한 모습을 보였습니다.",
            "식사 후 약 10분, 조용한 거실에서 아이가 혼자 놀면서도 집중력이 떨어지는 모습이 관찰되었습니다.",
            "오전 10시, 학교 앞에서 만난 아이가 아침 식사를 마친 후 에너지가 넘치는 모습을 보였습니다.",
            "저녁 식사 전, 아이가 놀이방에서 장난감을 조립하며 창의력을 발휘하는 모습이 인상적이었습니다.",
            "낮잠 후, 아이가 피곤한 표정을 지으며 활동에 참여하는 모습이 확인되었습니다.",
            "집안에서 가족과 함께 시간을 보내며 편안한 분위기 속에서 아이가 즐겁게 놀았습니다.",
            "오후 2시, 식사 전후의 에너지 변화가 뚜렷하게 나타난 모습을 보였습니다.",
            "아이의 식사 습관이 건강하며, 간식 시간 후에도 기운이 넘치는 모습을 확인했습니다.",
            "주변 소음이 적은 환경에서 아이가 집중하며 놀이에 몰입하는 모습이 인상적이었습니다.",
            "보호자와 함께 있을 때 아이가 더 안정되고 편안한 모습을 보였습니다.",
            "실내에서의 활동 후, 아이가 충분히 에너지를 발산한 후 차분해진 모습을 보였습니다.",
            "집 근처 도서관에서 조용히 책을 읽는 등 차분한 환경에서 긍정적인 정서가 나타났습니다.",
            "아이의 활동 전후 식사 상태에 따라 에너지 변화가 뚜렷하게 관찰되었습니다.",
            "오후 4시, 햇살 좋은 날씨에 야외에서 활동한 후 아이가 만족스러운 표정을 보였습니다.",
            "보호자의 주의 깊은 관찰 하에 아이가 안전하게 놀면서도 스스로를 잘 조절하는 모습을 보였습니다.",
            "아이의 식사 후 활동에서 활발한 움직임과 함께 집중력도 상승하는 것을 확인했습니다.",
            "정서 상태가 안정적이며, 주위 환경이 차분해 아이의 심리적 안정에 도움이 되었습니다.",
            "아이가 가족과 함께 있을 때 보다 혼자 놀 때 더 창의적이고 활발한 모습을 보였습니다.",
            "보호자가 관찰한 결과, 아이가 식사 후 기분 좋은 에너지를 발산하는 모습이 인상적이었습니다.",
            "아이의 놀이 시간 동안 주변 상황(소음, 온도 등)이 안정되어 정서적 안정이 유지되었습니다."
    };

    // 함께 있던 사람에 대한 설명 예시 (단어 혹은 짧은 구절로 누구인지만 명시)
    private static String[] companionDescriptions = {
            "할머니",
            "할아버지",
            "선생님",
            "친구 어머니",
            "친구 아버지",
            "이모",
            "삼촌",
            "언니",
            "오빠",
            "형제",
            "자매",
            "보호자",
            "교사",
            "아줌마",
            "친구",
            "친척",
            "돌보미",
            "가족",
            "상담사",
            "도우미"
    };

    // 분석 결과 예시 (20개 요소)
    // 각 요소는 아이의 작품에서 드러난 감정과 그에 따른 구체적인 관찰 내용을 한두 문장으로 설명합니다.
    public static String[] analysisResults = {
            "아이의 그림에서 밝은 색채와 유려한 선들이 드러나며, 기쁨과 희망이 느껴집니다.",
            "사진을 통해 아이가 스스로를 자신감 있게 표현하고 있어, 긍정적인 감정이 엿보입니다.",
            "작성한 글에서 슬픔과 외로움이 미묘하게 드러나, 내면의 아픔이 표현되었습니다.",
            "아이의 그림에서 불안과 두려움이 느껴지며, 약간의 긴장감과 혼란스러운 요소가 보입니다.",
            "사진에서 놀람이 나타나, 아이가 새로운 경험에 대해 경이로운 감정을 느끼고 있습니다.",
            "아이의 작품은 차분한 색조로 중립적인 감정을 표현하여, 안정된 심리 상태를 보여줍니다.",
            "작성한 글에서 혼란스러운 감정이 섬세하게 드러나, 내면의 갈등이 암시됩니다.",
            "그림에서는 불안의 느낌이 강하게 나타나, 선명한 대비와 날카로운 선들이 인상적입니다.",
            "사진 분석 결과, 아이는 지루함과 무기력함을 보이며 약간의 우울감이 감지됩니다.",
            "작품 전반에 걸쳐 흥분과 에너지가 넘치며, 역동적인 감정 표현이 돋보입니다.",
            "그림 속에서 수줍은 감정이 은은하게 표현되어, 조용하지만 내면에 깊은 감정이 있습니다.",
            "아이의 밝은 사진에서 기쁨과 자부심이 느껴지며, 활기찬 표정이 인상적입니다.",
            "작성한 글에서 슬픔과 함께 아련한 회한이 드러나, 정서적 지원이 필요해 보입니다.",
            "그림에서는 분노의 감정이 다소 격렬하게 표현되어, 스트레스 요인이 있을 수 있음을 시사합니다.",
            "사진에서 두려움이 느껴지며, 아이가 낯선 상황에서 불안감을 보이는 모습이 관찰됩니다.",
            "아이의 작품에서 놀람과 호기심이 동시에 나타나, 새로운 도전에 대한 기대감이 엿보입니다.",
            "작성한 글은 혼란스러운 감정과 함께 미묘한 희망의 기미도 보여, 복합적인 내면 상태를 반영합니다.",
            "그림에서는 중립적인 감정이 안정적으로 표현되어, 차분한 분위기를 전달합니다.",
            "사진 분석 결과, 불안과 호기심이 공존하는 모습을 보이며, 세밀한 감정 변화가 포착됩니다.",
            "작성한 글에서 수줍음과 내성적인 태도가 드러나, 조심스러운 심리 상태가 느껴집니다."
    };

    // 솔루션 예시 (20개 요소)
    // 각 요소는 아이의 작품에서 나타난 감정을 토대로, 보호자가 아이와 어떻게 대화하고 지원해야 하는지 두세 문장으로 제시합니다.
    public static String[] solutionTexts = {
            "아이의 밝은 감정 표현을 보고 긍정적으로 격려해 주세요. 자주 칭찬하고 함께 즐거운 활동을 계획해 보세요.",
            "사진에서 나타난 자신감을 지속할 수 있도록, 아이와 대화를 통해 성취감을 공유해 주세요. 꾸준한 격려가 큰 도움이 됩니다.",
            "아이의 글에서 드러난 슬픔에 대해 부드럽게 다가가, 감정을 나누는 시간을 가져 보세요. 필요하다면 전문가 상담을 고려할 수도 있습니다.",
            "불안과 두려움이 느껴지는 상황에서는 아이에게 안정감을 주는 것이 중요합니다. 차분하게 상황을 설명하고, 안전한 환경에서 감정을 풀 수 있도록 도와주세요.",
            "놀람과 호기심이 느껴지는 아이에게는 새로운 경험을 함께 시도해 보도록 격려해 주세요. 긍정적인 피드백과 함께 도전적인 활동을 추천합니다.",
            "차분한 분위기의 아이에게는 현재의 안정된 상태를 유지할 수 있도록 일상적인 루틴과 꾸준한 관심을 보여주세요.",
            "혼란스러운 감정이 표현된 경우, 아이의 이야기를 경청하고 함께 감정을 정리하는 시간을 가져 보세요. 감정 일지 쓰기를 제안해 보는 것도 좋습니다.",
            "불안한 감정이 강조된 그림을 보았을 때는, 심호흡 운동이나 명상 같은 안정 기술을 함께 시도해 보세요. 아이가 스스로 진정할 수 있도록 도와주세요.",
            "지루함이 보이는 경우, 아이에게 새로운 취미나 활동을 제안하여 관심을 유도해 주세요. 다양한 경험을 통해 에너지를 재충전할 수 있도록 지원합니다.",
            "흥분이 넘치는 아이에게는 에너지를 긍정적으로 활용할 수 있도록 규칙적인 활동과 충분한 휴식을 계획해 주세요. 체계적인 스케줄 관리가 도움이 됩니다.",
            "수줍은 감정이 드러난 아이에게는 작은 성공을 자주 칭찬하여 자신감을 심어 주세요. 부드럽고 따뜻한 대화로 아이의 불안을 덜어주세요.",
            "밝은 감정의 사진을 본다면, 아이의 긍정적인 모습을 유지하도록 격려하고 일상의 즐거움을 함께 나누세요. 꾸준한 칭찬과 보상이 중요합니다.",
            "슬픔이 드러난 글을 읽었다면, 아이의 감정을 존중하며 따뜻하게 위로해 주세요. 감정을 공유하고, 필요한 지원을 받을 수 있도록 안내합니다.",
            "분노가 나타난 경우, 아이가 자신의 감정을 안전하게 표현할 수 있도록 도와주세요. 침착하게 대화하며 감정 관리 기술을 함께 연습해 보세요.",
            "두려움이 느껴지는 아이에게는 차분한 목소리와 함께 현재 상황에 대해 명확히 설명해 주세요. 신뢰할 수 있는 지원을 통해 아이의 두려움을 완화할 수 있습니다.",
            "놀람과 호기심을 동시에 보이는 아이에게는 적극적인 경험과 도전을 장려하세요. 아이의 탐구심을 인정하고, 긍정적인 피드백을 제공해 주세요.",
            "혼란스러운 감정과 희망이 공존하는 아이에게는 일관된 지지와 이해를 보여주세요. 아이가 자신의 감정을 안정적으로 표현할 수 있도록 도와주는 것이 중요합니다.",
            "중립적인 감정이 나타난 아이에게는 현재의 안정된 상태를 유지하도록 지원하며, 일상에서 작은 기쁨을 발견할 수 있게 격려해 주세요.",
            "불안과 호기심이 공존하는 경우, 아이의 감정을 공감하며 동시에 안정적인 환경을 제공해 주세요. 규칙적인 일상과 신뢰할 수 있는 대화가 필요합니다.",
            "수줍음이 표현된 아이에게는 부드러운 격려와 함께 점진적으로 사회적 도전에 참여할 수 있도록 지원해 주세요. 작은 성공을 통해 자신감을 키울 수 있습니다."
    };

    private static String[] guardianFeedback = {
            "솔루션을 적용한 후, 아이가 밝게 웃으며 자신감을 되찾은 것 같아요.",
            "아이의 표정이 한결 편안해졌고, 대화할 때도 자연스러워졌습니다.",
            "슬픔에 잠겼던 아이가 점차 미소를 되찾아 마음이 놓입니다.",
            "불안했던 아이가 조용한 대화와 따뜻한 격려 덕분에 안정감을 보였습니다.",
            "새로운 경험에 대한 호기심이 넘쳐, 아이가 활발하게 활동하고 있습니다.",
            "일상적인 루틴과 꾸준한 관심 덕분에 아이의 감정이 안정되었습니다.",
            "혼란스러운 감정이 서서히 정리되고, 아이가 명확하게 자신의 감정을 표현합니다.",
            "불안한 모습이 줄어들고, 아이가 스스로 진정하는 모습을 보니 다행입니다.",
            "아이의 에너지가 긍정적으로 변화하며, 새로운 취미에도 관심을 보입니다.",
            "체계적인 일상 관리 덕분에 아이가 규칙적인 생활을 하게 되어 기쁩니다.",
            "수줍음이 점차 극복되어 아이가 친구들과의 상호작용에 적극적으로 참여하고 있습니다.",
            "밝은 표정과 함께 아이가 주변 사람들과 더 따뜻하게 소통하는 모습이 인상적입니다.",
            "슬픔이 완화되어 아이가 편안하게 웃는 모습을 보니 마음이 놓입니다.",
            "분노 조절에 성공하며, 아이가 침착하게 감정을 표현하는 모습을 확인했습니다.",
            "두려움이 줄어들어 아이가 새로운 환경에도 자신감을 가지고 임하고 있습니다.",
            "놀람과 호기심이 긍정적인 도전으로 이어져, 아이가 즐겁게 활동하고 있습니다.",
            "혼란스러운 감정이 정리되면서 아이의 마음이 한층 평온해진 것 같습니다.",
            "중립적인 상태에서 아이가 자신을 잘 표현하고, 안정적인 정서를 유지합니다.",
            "불안감이 크게 완화되어 아이가 더욱 자유롭게 생활하는 모습을 보입니다.",
            "수줍음이 극복되어, 아이가 자신있게 친구들과 교류하는 모습이 매우 기쁩니다."
    };

    private static String[] diaryEntries = {
            "오늘은 맑은 하늘 아래에서 아이와 함께 공원에 다녀왔어요. 아이가 신나게 뛰어놀며 즐거워하는 모습이 너무 보기 좋았어요.",
            "날씨가 쌀쌀한 아침, 아이와 따뜻한 커피 한 잔을 나누며 집에서 조용한 시간을 보냈습니다. 아이가 숙제를 스스로 하려는 모습이 인상적이었어요.",
            "오늘은 아이가 학교에서 돌아와 집안에서 조용히 책을 읽고, 숙제도 스스로 하는 등 자립심을 키워가는 모습이 자랑스러웠습니다.",
            "아이와 함께 맛있는 점심을 먹고 동네를 산책했어요. 아이의 웃음소리가 하루의 피로를 잊게 해주었습니다.",
            "아이의 학교 생활 소식을 들으니 오늘은 새로운 친구를 사귀었다더군요. 아이가 활발하게 친구들과 어울리는 모습을 보니 기분이 좋았습니다.",
            "비가 오는 날, 아이와 함께 집에서 퍼즐 놀이와 그림 그리기를 하며 아늑한 시간을 보냈어요. 창밖의 빗소리가 편안함을 주었습니다.",
            "오늘은 아이가 집안일을 도우며 적극적인 모습을 보였습니다. 작은 성공이 쌓이는 것 같아 보호자로서 뿌듯했습니다.",
            "아이와 함께 소풍 준비를 하며 기대에 찬 모습을 보았어요. 새로운 경험을 통해 아이가 즐거워하는 모습이 인상 깊었습니다.",
            "아이와 함께 간단한 요리를 해보았어요. 아이가 재료를 만지작거리며 즐거워하는 모습을 보니 하루가 행복했어요.",
            "오늘은 아이가 조금 지친 듯 보여 따뜻한 말 한마디와 푹 쉬게 해주었어요. 내 마음도 함께 편안해졌습니다.",
            "아이의 창의적인 그림과 이야기를 들으며 감동받은 하루였습니다. 아이의 상상력이 무한하다는 사실이 정말 놀라웠어요.",
            "오늘은 아이가 놀이터에서 혼자 놀며 자신감을 쌓는 모습을 보았어요. 자립심을 키우는 과정이 보기 좋았습니다.",
            "아이와 함께 동화책을 읽으며 상상의 나래를 펼쳤어요. 아이의 눈빛이 반짝이는 순간이 오늘의 행복이었어요.",
            "오늘은 박물관에 다녀왔어요. 아이가 전시물에 큰 관심을 보이며 새로운 지식을 습득하는 모습이 인상적이었습니다.",
            "아이가 평소보다 더욱 활발하게 놀았어요. 건강한 에너지가 넘치는 모습이 너무 기뻤습니다.",
            "아이와 함께 산책하며 자연의 소리와 색을 감상했어요. 작은 꽃에도 신기함을 보이는 아이의 순수함이 마음을 따뜻하게 했습니다.",
            "오늘은 아이가 자신의 감정을 솔직하게 표현하는 모습을 보며 마음이 한층 편안해졌어요.",
            "저녁 시간, 아이와 따뜻한 대화를 나누며 하루의 피로를 풀었습니다. 아이와의 소중한 시간이 정말 값졌어요.",
            "오늘은 아이가 작은 실수에도 낙담하는 모습을 보았어요. 따뜻한 격려와 관심으로 아이를 위로해주었습니다.",
            "아이와 함께 웃고, 때로는 함께 고민하며 보낸 하루였습니다. 아이의 성장을 응원하며 앞으로도 늘 사랑할 거예요."
    };

    private static String[] diaryTitles = {
            "맑은 날의 공원 나들이",
            "따뜻한 아침의 시작",
            "자립심이 돋보인 하루",
            "즐거운 점심 시간",
            "새로운 친구와의 만남",
            "비 오는 날의 아늑함",
            "작은 성공의 기쁨",
            "예비 소풍 준비",
            "요리 도전의 즐거움",
            "편안한 오후의 휴식",
            "창의력이 빛나는 순간",
            "놀이터에서 자신감 UP",
            "상상력을 자극하는 이야기",
            "박물관에서 배운 하루",
            "활기찬 에너지 넘치는 시간",
            "자연과의 만남",
            "솔직한 감정 표현",
            "따뜻한 저녁 대화",
            "작은 실수, 큰 배움",
            "웃음과 고민의 하루"
    };

    private static String[] doctorOpinions = {
            "성장 발달 양호",
            "예방 접종 일정 확인 필요",
            "철분 섭취 권장",
            "비타민 D 섭취 권장",
            "수면 습관 개선 필요",
            "정기적인 구강 검진 필요",
            "알레르기 유발 식품 주의",
            "언어 발달 촉진 필요",
            "사회성 발달 촉진 필요",
            "안전 사고 예방 교육 필요",
            "정상 체중 유지",
            "정상 신장 유지",
            "정상 머리둘레 유지",
            "정상 시력 유지",
            "정상 청력 유지",
            "정상 혈압 유지",
            "정상 맥박 유지",
            "정상 호흡 유지",
            "정상 체온 유지",
            "다음 검진 일정 안내"
    };

    private static String[] guideTitles = {
            "iLog 서비스 소개",
            "회원가입 및 로그인 방법",
            "아동 정보 등록하기",
            "보호자와 교사 계정 연결하기",
            "심리 설문 작성 방법",
            "감정 분석 기능 사용하기",
            "그림 분석 기능 사용하기",
            "글 분석 기능 사용하기",
            "표정 분석 기능 사용하기",
            "분석 결과 확인 및 관리",
            "통계 데이터 활용하기",
            "대시보드 사용 방법",
            "권한 관리 설정하기",
            "알림 설정 및 관리",
            "개인정보 보호 정책",
            "데이터 백업 및 복구",
            "자주 발생하는 문제 해결하기",
            "모바일 앱 사용 방법",
            "결제 및 구독 관리",
            "고객 지원 및 문의하기"
    };

    // 이용안내글 내용 배열 (크기 20)
    private static String[] guideContents = {
            "iLog는 아동의 감정과 발달을 체계적으로 기록하고 분석하여 건강한 성장을 돕는 서비스입니다. 글, 그림, 표정 분석을 통해 아이의 심리 상태를 파악하고, 맞춤형 솔루션을 제공합니다.",
            "iLog 서비스 이용을 위해서는 회원가입이 필요합니다. 홈페이지 우측 상단의 '회원가입' 버튼을 클릭하여 이메일, 비밀번호, 이름 등의 정보를 입력하세요. 가입 후 로그인하여 서비스를 이용할 수 있습니다.",
            "로그인 후 '아동 관리' 메뉴에서 '아동 등록' 버튼을 클릭하여 아동의 이름, 생년월일, 성별 등의 기본 정보를 입력합니다. 아동 정보는 언제든지 수정할 수 있습니다.",
            "교사와 보호자 계정을 연결하려면 '권한 관리' 메뉴에서 '초대하기' 기능을 사용하세요. 이메일 주소를 입력하여 초대장을 발송할 수 있으며, 상대방이 수락하면 연결이 완료됩니다.",
            "심리 설문은 '심리 설문' 메뉴에서 진행할 수 있습니다. 아동을 선택한 후 적합한 설문 유형을 선택하고 질문에 답변하세요. 완료 후 결과를 즉시 확인할 수 있습니다.",
            "감정 분석을 위해 '분석하기' 메뉴에서 '감정 분석' 옵션을 선택하세요. 아동의 글, 그림, 표정 중 분석하고자 하는 유형을 선택하고 파일을 업로드하면 AI가 분석 결과를 제공합니다.",
            "그림 분석은 아동이 그린 그림을 업로드하여 진행합니다. '분석하기' 메뉴에서 '그림 분석'을 선택하고 이미지 파일을 업로드하세요. 색상 사용, 구도, 표현 방식 등을 분석하여 아동의 심리 상태를 파악합니다.",
            "글 분석은 아동이 작성한 일기, 편지 등의 텍스트를 분석합니다. '분석하기' 메뉴에서 '글 분석'을 선택하고 텍스트를 입력하거나 파일을 업로드하세요. 단어 선택, 문장 구조 등을 분석하여 감정 상태를 파악합니다.",
            "표정 분석은 아동의 얼굴 사진이나 영상을 통해 감정을 분석합니다. '분석하기' 메뉴에서 '표정 분석'을 선택하고 사진이나 영상을 업로드하세요. 얼굴 표정을 분석하여 감정 상태를 파악합니다.",
            "분석 결과는 '결과 목록' 메뉴에서 확인할 수 있습니다. 날짜, 분석 유형 등으로 필터링하여 원하는 결과를 찾을 수 있으며, 각 결과를 클릭하면 상세 내용과 추천 솔루션을 확인할 수 있습니다.",
            "통계 데이터는 '통계' 메뉴에서 확인할 수 있습니다. 기간별, 분석 유형별로 데이터를 필터링하여 아동의 감정 변화 추이를 그래프로 확인할 수 있습니다. 데이터는 CSV 형식으로 내보낼 수도 있습니다.",
            "대시보드는 아동의 전반적인 상태를 한눈에 파악할 수 있는 페이지입니다. 최근 분석 결과, 감정 변화 추이, 설문 결과 등이 요약되어 표시됩니다. 대시보드에서 각 항목을 클릭하면 상세 페이지로 이동합니다.",
            "권한 관리는 '설정' 메뉴의 '권한 관리'에서 할 수 있습니다. 다른 사용자(교사, 보호자 등)에게 아동 정보 접근 권한을 부여하거나 회수할 수 있으며, 권한 수준(읽기 전용, 편집 가능 등)을 설정할 수 있습니다.",
            "알림 설정은 '설정' 메뉴의 '알림 관리'에서 할 수 있습니다. 새로운 분석 결과, 설문 완료, 권한 요청 등 다양한 이벤트에 대한 알림을 이메일이나 앱 푸시 알림으로 받을 수 있습니다.",
            "iLog는 사용자의 개인정보 보호를 최우선으로 합니다. 모든 데이터는 암호화되어 저장되며, 권한이 있는 사용자만 접근할 수 있습니다. 자세한 내용은 '개인정보 처리방침'을 참고하세요.",
            "데이터 백업은 '설정' 메뉴의 '데이터 관리'에서 할 수 있습니다. 모든 분석 결과와 설문 데이터를 ZIP 파일로 내보내거나, 클라우드 스토리지에 자동 백업되도록 설정할 수 있습니다.",
            "서비스 이용 중 문제가 발생하면 '고객 지원' 메뉴의 '자주 묻는 질문'을 참고하세요. 로그인 오류, 파일 업로드 실패, 분석 결과 오류 등 자주 발생하는 문제의 해결 방법을 확인할 수 있습니다.",
            "iLog 모바일 앱은 iOS와 Android에서 모두 사용할 수 있습니다. 앱 스토어나 구글 플레이에서 'iLog'를 검색하여 설치하세요. 모바일 앱에서도 웹 버전과 동일한 기능을 사용할 수 있습니다.",
            "결제 및 구독 관리는 '설정' 메뉴의 '구독 관리'에서 할 수 있습니다. 현재 구독 상태 확인, 플랜 변경, 결제 수단 관리, 영수증 확인 등의 기능을 이용할 수 있습니다.",
            "고객 지원이 필요한 경우 '고객 지원' 메뉴에서 문의하기 기능을 이용하세요. 이메일(support@ilog.co.kr) 또는 전화(02-123-4567)로도 문의할 수 있으며, 평일 09:00-18:00에 실시간 채팅 상담도 가능합니다."
    };

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
    private final MentalSurveyRepository mentalSurveyRepository;
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
        // 이용안내 초기화
        initializeGuideEntities();

        // FamilyBackGround와 AnalysisType 초기 데이터 생성
        initializeFamilyBackgrounds();
        initializeAnalysisTypes();

        // 일반 회원(엄마) 및 자녀, 그리고 설문/분석/기록 데이터 생성
        for (int i = 1; i <= 1; i++) {
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
                .name("관리자")
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
        String[] firstNames = {"김", "이"};
        String[] lastNames = {"수연", "유진"};
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        String fullName = firstName + " " + lastName;
        String signInId = (firstName + "_" + lastName).toLowerCase() + "_" + random.nextInt(1000);
        String email = firstName.toLowerCase() + lastName.toLowerCase() + random.nextInt(100) + "@parent.com";

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
        // 각 mom마다 2명의 자녀 생성
        for (int j = 0; j < 2; j++) {
            String[] firstNames = {"김", "이"};
            String[] lastNames = {"지호", "지영"};
            String childName = firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
            String birthLocation = locations[random.nextInt(locations.length)];

            ChildEntity child = ChildEntity.builder()
                    .name(childName)
                    .birthDate(now.minusYears(random.nextInt(3) + 1))
                    .birthLocation(birthLocation)
                    .note("발달 장애가 있음. " + random.nextInt(100))
                    .originalProfileImgName(childName.contains("지호") ? "jiho-0.jpg" : "jiyoung-0.jpeg")
                    .savedProfileImgName(childName.contains("지호") ? "jiho-0.jpg" : "jiyoung-0.jpeg")
                    .gender(childName.contains("지호") ? Gender.MAN : Gender.WOMAN)
                    .callName("엄마")
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
//            createMentalSurveyResponses(child, mom, random);

            // AnalysisTarget 및 관련 데이터 생성
            createAnalysisTargets(child, mom, random, now);

            // ChildDiary, ChildRecord, HealthCheck 생성
            createChildDiaryEntities(child, mom, random, now);
            createChildRecordAndHealthCheck(child, mom, random, now);
        }
    }

    private void createMentalSurveyResponses(ChildEntity child, MemberEntity respondent, ThreadLocalRandom random) {
        LocalDateTime now = LocalDateTime.now();
        //한달치의 설문조사 데이터 생성
        LocalDateTime startDate = now.minusDays(30);
        int childAge = AgeCalculator.calculateAge(child.getBirthDate());
        List<MentalSurveyEntity> mentalSurveys = mentalSurveyRepository
                .findByTitleContainingAndType(Integer.toString(childAge), respondent.getRelationType());
//        Set<ChildEntity> childSet = respondent.getRelationShips().stream()
//                .map(relationShip -> relationShip.getChild()).collect(Collectors.toSet());

        for (MentalSurveyEntity mentalSurvey : mentalSurveys) {
            for (LocalDateTime date = startDate; date.isBefore(now.plusDays(1)); date = date.plusDays(1)) {
                List<SectionResponse> sectionResponses = new ArrayList<>();
                // 섹션 1
                MentalSurveySection section1 = mentalSurvey.getSections().get(0);
                List<MentalSurveyQuestion> questions1 = section1.getQuestions();
                List<QuestionResponse> qResponses1 = Arrays.asList(
                        new QuestionResponse(questions1.get(0).getItem(), questions1.get(0).getExample(), random.nextInt(1, 6)),
                        new QuestionResponse(questions1.get(1).getItem(), questions1.get(1).getExample(), random.nextInt(1, 6)),
                        new QuestionResponse(questions1.get(2).getItem(), questions1.get(2).getExample(), random.nextInt(1, 6)),
                        new QuestionResponse(questions1.get(3).getItem(),questions1.get(3).getExample(), random.nextInt(1, 6))
                );
                int section1Score = qResponses1.stream().mapToInt(QuestionResponse::getScore).sum();
                sectionResponses.add(new SectionResponse(section1.getTitle(), qResponses1, section1Score));

                // 섹션 2
                MentalSurveySection section2 = mentalSurvey.getSections().get(1);
                List<MentalSurveyQuestion> questions2 = section2.getQuestions();
                List<QuestionResponse> qResponses2 = Arrays.asList(
                        new QuestionResponse(questions2.get(0).getItem(), questions2.get(0).getExample(), random.nextInt(1, 6)),
                        new QuestionResponse(questions2.get(1).getItem(), questions2.get(1).getExample(), random.nextInt(1, 6)),
                        new QuestionResponse(questions2.get(2).getItem(), questions2.get(2).getExample(), random.nextInt(1, 6)),
                        new QuestionResponse(questions2.get(3).getItem(),questions2.get(3).getExample(), random.nextInt(1, 6))
                );
                int section2Score = qResponses2.stream().mapToInt(QuestionResponse::getScore).sum();
                sectionResponses.add(new SectionResponse(section2.getTitle(), qResponses2, section2Score));

                // 섹션 3
                MentalSurveySection section3 = mentalSurvey.getSections().get(2);
                List<MentalSurveyQuestion> questions3 = section3.getQuestions();
                List<QuestionResponse> qResponses3 = Arrays.asList(
                        new QuestionResponse(questions3.get(0).getItem(), questions3.get(0).getExample(), random.nextInt(1, 6)),
                        new QuestionResponse(questions3.get(1).getItem(), questions3.get(1).getExample(), random.nextInt(1, 6)),
                        new QuestionResponse(questions3.get(2).getItem(), questions3.get(2).getExample(), random.nextInt(1, 6)),
                        new QuestionResponse(questions3.get(3).getItem(),questions3.get(3).getExample(), random.nextInt(1, 6))
                );
                int section3Score = qResponses3.stream().mapToInt(QuestionResponse::getScore).sum();
                sectionResponses.add(new SectionResponse(section3.getTitle(), qResponses3, section3Score));

                int totalScore = sectionResponses.stream().mapToInt(SectionResponse::getSectionLikertScore).sum();
                String diagnosisComment;
                if (totalScore < 15) {
                    diagnosisComment = "위험";
                } else if (totalScore < 30) {
                    diagnosisComment = "주의 필요";
                } else if (totalScore < 45) {
                    diagnosisComment = "정상";
                } else {
                    diagnosisComment = "양호";
                }
                String comment = new StringBuilder()
                        .append("심리 평가 결과 총점 ")
                        .append(totalScore)
                        .append(" ")
                        .append(diagnosisComment).toString();

                MentalSurveyResponseEntity response = MentalSurveyResponseEntity.builder()
                        .surveyId(mentalSurvey.getId())
                        .surveyTitle(mentalSurvey.getTitle())
                        .relationType(mentalSurvey.getType().toString())
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
    }

    private void createAnalysisTargets(ChildEntity child, MemberEntity uploader, ThreadLocalRandom random, LocalDateTime now) {
        EmotionType[] emotionTypes = EmotionType.values();
        List<AnalysisType> analysisTypeList = new ArrayList<>(Arrays.asList(AnalysisType.values()));

        for (int k = 0; k < 20; k++) {
            AnalysisTargetEntity target = AnalysisTargetEntity.builder()
                    .child(child)
                    .registerDate(now.minusDays(random.nextInt(30)))
                    .uploadedBy(uploader)
                    .originalTargetFileName(String.format("test-target-%d.jpeg", k))
                    .savedTargetFileName(String.format("test-target-%d.jpeg", k))
                    .supplement(DataInitializer.supplementaryComments[k])
                    .companion(DataInitializer.companionDescriptions[k])
                    .build();
            analysisTargetRepository.save(target);

            Collections.shuffle(analysisTypeList);
            List<AnalysisType> selectedTypes = analysisTypeList.subList(0, 2);
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
                    .weatherDesc("오늘의 날씨는, " + (random.nextBoolean() ? "맑음" : "흐림"))
                    .build();
            target.setWeather(weather);

            AnalysisResultEntity analysisResult = AnalysisResultEntity.builder()
                    .emotionScore(random.nextDouble())
                    .analysisTarget(target)
                    .title("감정 분석-".concat(UUID.randomUUID().toString()))
                    .analysisResultText(DataInitializer.analysisResults[k])
                    .suggestedSolution(DataInitializer.solutionTexts[k])
                    .emotionType(emotionTypes[k % 11])
                    .build();
            analysisResultRepository.save(analysisResult);

            AnalysisResultNoteEntity note = AnalysisResultNoteEntity.builder()
                    .analysisResult(analysisResult)
                    .content(DataInitializer.guardianFeedback[k])
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
                    .content(DataInitializer.diaryEntries[k])
                    .title(DataInitializer.diaryTitles[k])
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
                    .note(DataInitializer.doctorOpinions[k])
                    .build();
            childRecordRepository.save(record);

            HealthCheckEntity healthCheck = HealthCheckEntity.builder()
                    .child(child)
                    .childRecord(record)
                    .member(author)
                    .originalFileName(String.format("test-healthCheck-%d.jpeg",k % 4))
                    .savedFileName(String.format("test-healthCheck-%d.jpeg",k % 4))
                    .build();
            childHealthCheckRepository.save(healthCheck);
        }
    }

    private void initializeClaims(ThreadLocalRandom random, LocalDateTime now) {
        // 각 mom 당 한 건의 Claim 및 ClaimAnswer 생성
        // 이 메서드는 필요에 따라 별도로 분리할 수 있습니다.
    }

    private void initializeGuideEntities() {
        for (int i = 0; i < 10; i++) {
            GuideEntity guide = GuideEntity.builder()
                    .author(memberRepository.findBySignInId("ADMIN").orElse(null))
                    .title(guideTitles[i])
                    .content(guideContents[i])
                    .build();
            guideRepository.save(guide);
        }
    }

    private void initializeTeacherAccounts(ThreadLocalRandom random, LocalDateTime now) {
        // 교사 계정 생성
        MemberEntity teacher = MemberEntity.builder()
                .signInId("teacherKim")
                .password(passwordEncoder.encode("Teacher1!"))
                .name("김선생님")
                .email("teacherkim@teacher.com")
                .relationType(RelationType.TEACHER)
                .personalInformationCollectionAndUsageAgreement(true)
                .build();
        memberRepository.save(teacher);
        this.testTeacherSignInId = "teacherKim";

        // 기존에 저장된 아동 중 하나를 선택하여 교사와 관계 생성
        List<ChildEntity> existingChildren = childRepository.findAll();
        for (ChildEntity existingChild : existingChildren) {
            // 예시로 첫 번째 아동을 선택 (필요에 따라 다른 조건을 적용할 수 있음)
            RelationShipEntity teacherChildRelation = RelationShipEntity.builder()
                    .member(teacher)
                    .child(existingChild)
                    .permissionLevel(PermissionLevel.VIEWER)
                    .relationType(RelationType.TEACHER)
                    .build();
            relationShipRepository.save(teacherChildRelation);

//            createMentalSurveyResponses(existingChild, teacher, random);
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