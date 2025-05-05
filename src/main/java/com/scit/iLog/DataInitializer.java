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
import com.scit.iLog.domain.mentalsurvey.*;
import com.scit.iLog.domain.permission.PermissionRequestEntity;
import com.scit.iLog.domain.permission.PermissionRequestStatus;
import com.scit.iLog.domain.sentimentalAnalysis.*;
import com.scit.iLog.exception.MemberNotFoundException;
import com.scit.iLog.repository.*;
import com.scit.iLog.util.AgeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private static final String[] analysisResultTitles = {
            "私の子ども、太陽のような笑顔",
            "愛らしいわが子の喜び",
            "静かな涙、子どもの悲しみ",
            "恐れの中でも咲く勇気",
            "驚きに満ちた今日",
            "穏やかな心、わが子の物語",
            "混乱の中に芽生えた小さな希望",
            "不安を乗り越えた笑顔",
            "退屈を乗り越えて成長する姿",
            "エネルギーあふれる元気な一日",
            "恥ずかしがり屋な心、温かな愛情",
            "わが子の真心が込められた一日",
            "悲しみの中でも輝く光",
            "怒りを超えた愛の力",
            "恐怖に立ち向かった勇敢な瞬間",
            "驚きと好奇心に満ちた一日",
            "内面の声を伝えてくれた時間",
            "静かだが温かい心の記録",
            "複雑な感情を愛で解きほぐして",
            "恥ずかしがり屋の愛、真心のこもった物語"
    };
    // 위도 배열 (예시 값: 실제 상황에 맞게 수정 가능)
    private static final double[] LATITUDES = {
            37.5665, 35.1796, 35.1595, 37.4563, 35.8714,
            36.3504, 35.5384, 36.3504, 35.8401, 35.1550,
            37.4563, 35.1796, 35.1595, 37.5665, 36.3504,
            35.8401, 35.5384, 35.1550, 35.8714, 37.4563
    };
    // 경도 배열 (예시 값: 실제 상황에 맞게 수정 가능)
    private static final double[] LONGITUDES = {
            126.9780, 129.0756, 129.0756, 126.7052, 129.0756,
            127.3845, 126.8494, 127.3845, 128.5918, 126.7848,
            126.7052, 129.0756, 129.0756, 126.9780, 127.3845,
            128.5918, 126.8494, 126.7848, 129.0756, 126.7052
    };
    // 위치 이름 배열 (예시: "서울", "부산", 등)
    private static final String[] LOCATION_NAMES = {
            "東京",   // 서울
            "大阪",   // 부산
            "名古屋", // 대구
            "横浜",   // 인천
            "福岡",   // 광주
            "神戸",   // 대전
            "札幌",   // 울산
            "川崎",   // 세종
            "埼玉",   // 경기
            "長野",   // 강원
            "群馬",   // 충북
            "栃木",   // 충남
            "静岡",   // 전북
            "岡山",   // 전남
            "広島",   // 경북
            "熊本",   // 경남
            "沖縄",   // 제주
            "大阪1",  // 부산1
            "名古屋1",// 대구1
            "東京1"   // 서울1
    };
    // 분석 결과 예시 (20개 요소)
    // 각 요소는 아이의 작품에서 드러난 감정과 그에 따른 구체적인 관찰 내용을 한두 문장으로 설명합니다.
    public static String[] analysisResults = {
            "子どもの絵から明るい色彩と滑らかな線が見られ、喜びと希望が感じられます。",
            "写真から自信を持って自分を表現する姿がうかがえ、ポジティブな感情が読み取れます。",
            "書かれた文章から悲しみや孤独が微妙に現れ、内面的な痛みが表現されています。",
            "子どもの絵から不安と恐れが感じられ、若干の緊張感や混乱した要素が見られます。",
            "写真に現れた驚きは、新しい経験に対する感動がうかがえます。",
            "子どもの作品は落ち着いた色合いで中立的な感情を表現し、安定した心理状態を示しています。",
            "書かれた文章に混乱した感情が繊細に表現され、内面の葛藤が暗示されています。",
            "絵からは不安が強く感じられ、コントラストや鋭い線が印象的です。",
            "写真分析の結果、子どもは退屈さや無気力を見せ、少しの憂うつさが感じられます。",
            "作品全体に興奮とエネルギーが溢れており、ダイナミックな感情表現が際立っています。",
            "絵の中に恥ずかしさがほのかに表現され、静かだけど深い感情があります。",
            "明るい写真から喜びと誇りが感じられ、元気な表情が印象的です。",
            "書かれた文章に悲しみとともに淡い後悔がにじみ出ており、感情的な支援が必要に見えます。",
            "絵からは怒りの感情がやや激しく表現され、ストレス要因がある可能性が示唆されます。",
            "写真から恐れが感じられ、子どもが慣れない状況に不安を感じている様子が観察されます。",
            "子どもの作品から驚きと好奇心が同時に現れ、新しい挑戦への期待がうかがえます。",
            "書かれた文章は混乱した感情の中に希望の兆しも見られ、複雑な内面状態を反映しています。",
            "絵では中立的な感情が安定して表現され、落ち着いた雰囲気が伝わります。",
            "写真分析の結果、不安と好奇心が共存する様子が見られ、細やかな感情の変化が捉えられます。",
            "書かれた文章に恥ずかしさや内向的な態度が現れ、慎重な心理状態が感じられます。"
    };
    // 솔루션 예시 (20개 요소)
    // 각 요소는 아이의 작품에서 나타난 감정을 토대로, 보호자가 아이와 어떻게 대화하고 지원해야 하는지 두세 문장으로 제시합니다.
    public static String[] solutionTexts = {
            "子どもの明るい感情表現を見て、積極的に褒めてあげてください。一緒に楽しい活動を計画しましょう。",
            "写真に見られる自信を維持できるように、子どもと達成感を共有する会話をしてください。継続的な励ましが役立ちます。",
            "文章に見られる悲しみに対しては、優しく寄り添いながら感情を分かち合う時間を持ってください。必要であれば専門家に相談を。",
            "不安や恐れが感じられる場合、安心感を与えることが大切です。状況を丁寧に説明し、安全な環境で感情を発散させましょう。",
            "驚きや好奇心が見られる子どもには、新しい経験を一緒に試すよう励ましてください。前向きなフィードバックも忘れずに。",
            "落ち着いた子どもには、現在の安定した状態を維持できるように、日常的なルーチンと継続的な関心を示しましょう。",
            "混乱した感情が表現されている場合、子どもの話に耳を傾けて感情を整理する時間を設けましょう。感情日記を書くのも有効です。",
            "不安が強く出ている絵を見たときは、深呼吸や瞑想などの安定スキルを一緒に試してみてください。自己調整を促しましょう。",
            "退屈が見られる場合は、新しい趣味や活動を提案して興味を引き出しましょう。多様な経験でエネルギーを回復させます。",
            "興奮が強い子どもには、エネルギーを前向きに使えるよう、規則的な活動と十分な休息を組み込みましょう。スケジュール管理が助けになります。",
            "恥ずかしがり屋の子どもには、小さな成功を褒めて自信を育てましょう。穏やかで温かい対話で不安を和らげてください。",
            "明るい写真を見たら、子どものポジティブな面を維持できるよう励まし、日常の楽しさを共有してください。",
            "悲しみが表れた文章を読んだら、子どもの感情を尊重し、温かく慰めてください。必要な支援を案内しましょう。",
            "怒りが見られる場合は、安全に感情を表現できるようサポートし、落ち着いて会話しながら感情コントロールを一緒に練習してください。",
            "恐れが見える子どもには、落ち着いた声で状況を説明し、信頼できる支援を通じて不安を和らげましょう。",
            "驚きと好奇心を見せる子どもには、積極的な経験や挑戦を勧めましょう。探究心を認めて、前向きなフィードバックを与えてください。",
            "混乱と希望が混在する子どもには、一貫した支援と理解を示してください。感情を安定して表現できるよう助けることが大切です。",
            "中立的な感情が見られる子どもには、安定した状態を維持できるように支援し、日常の中で小さな喜びを見つけられるよう励ましてください。",
            "不安と好奇心が共存する場合、感情に共感しつつ安定した環境を提供してください。規則正しい生活と信頼できる会話が必要です。",
            "恥ずかしさが表現された子どもには、優しい励ましとともに少しずつ社会的な挑戦に参加できるよう支援しましょう。小さな成功が自信に繋がります。"
    };
    // 보충 설명 예시 (보호자가 관찰 시 제공하는 추가 정보)
    // 예: 관찰 시각, 장소, 식사 여부, 환경 등 자세한 내용을 기술
    private static final String[] supplementaryComments = {
            "午後3時30分、自宅近くの公園で子どもがおやつを食べた後、元気に遊ぶ様子が見られました。",
            "食後約10分、静かなリビングで一人遊びをしていましたが、集中力が落ちている様子が観察されました。",
            "午前10時、学校の前で出会った子どもは朝食を済ませ、エネルギーに満ちた様子でした。",
            "夕食前、子どもが遊び部屋でおもちゃを組み立てながら創造力を発揮している様子が印象的でした。",
            "昼寝の後、子どもが疲れた表情で活動に参加している姿が確認されました。",
            "家の中で家族と一緒に時間を過ごし、落ち着いた雰囲気の中で子どもが楽しそうに遊んでいました。",
            "午後2時、食事の前後でエネルギーの変化がはっきりと見られました。",
            "子どもの食習慣は健康的で、おやつの後でも元気いっぱいの様子が確認されました。",
            "周囲の騒音が少ない環境で子どもが集中して遊びに没頭している様子が印象的でした。",
            "保護者と一緒にいるとき、子どもがより安定してリラックスした様子を見せていました。",
            "室内活動の後、子どもが十分にエネルギーを発散し、落ち着いた様子が見られました。",
            "自宅近くの図書館で静かに本を読むなど、穏やかな環境の中でポジティブな感情が見られました。",
            "子どもの活動の前後で、食事状態によるエネルギーの変化が明確に観察されました。",
            "午後4時、晴れた天気の中で屋外活動をした後、子どもが満足そうな表情を見せていました。",
            "保護者の注意深い観察のもとで、子どもが安全に遊びながら自己調整する姿が見られました。",
            "食後の活動で、子どもが活発に動きながら集中力も高まっていることが確認されました。",
            "感情の状態は安定しており、周囲の環境が静かで、子どもの心理的安定に役立っていました。",
            "子どもは家族と一緒にいるよりも、一人で遊んでいるときの方が創造的で活発な様子が見られました。",
            "保護者の観察により、子どもが食後に気持ちの良いエネルギーを発散している様子が印象的でした。",
            "子どもの遊び時間中、周囲の環境（騒音、温度など）が安定しており、情緒的な安定が保たれていました。"
    };
    // 함께 있던 사람에 대한 설명 예시 (단어 혹은 짧은 구절로 누구인지만 명시)
    private static final String[] companionDescriptions = {
            "おばあちゃん",
            "おじいちゃん",
            "先生",
            "友だちのお母さん",
            "友だちのお父さん",
            "おば",
            "おじ",
            "お姉さん",
            "お兄さん",
            "兄弟",
            "姉妹",
            "保護者",
            "教師",
            "近所のおばさん",
            "友だち",
            "親戚",
            "ベビーシッター",
            "家族",
            "カウンセラー",
            "ヘルパー"
    };
    private static final String[] guardianFeedback = {
            "ソリューションを適用した後、子どもが明るく笑い、自信を取り戻したように感じます。",
            "子どもの表情が一段と穏やかになり、会話も自然になりました。",
            "悲しみに沈んでいた子どもが徐々に笑顔を取り戻し、安心しています。",
            "不安だった子どもが静かな会話と温かい励ましのおかげで落ち着きを見せました。",
            "新しい体験に対する好奇心があふれ、子どもが元気に活動しています。",
            "日常のルーティンと継続的な関心のおかげで、子どもの感情が安定しました。",
            "混乱した感情が徐々に整理され、子どもが自分の感情を明確に表現するようになりました。",
            "不安な様子が減り、子どもが自ら落ち着く様子を見て安心しました。",
            "子どものエネルギーが前向きに変化し、新しい趣味にも興味を示しています。",
            "計画的な日常管理のおかげで、子どもが規則正しい生活を送るようになり嬉しいです。",
            "恥ずかしがり屋な性格が徐々に克服され、子どもが友達との交流に積極的に参加しています。",
            "明るい表情と共に、子どもが周囲の人とより温かくコミュニケーションを取る姿が印象的です。",
            "悲しみが和らぎ、子どもが穏やかに笑う姿を見て安心しました。",
            "怒りのコントロールに成功し、子どもが冷静に感情を表現する様子が確認できました。",
            "恐怖心が減り、子どもが新しい環境にも自信を持って取り組んでいます。",
            "驚きと好奇心が前向きな挑戦につながり、子どもが楽しそうに活動しています。",
            "混乱した感情が整理され、子どもの心がより穏やかになったように感じます。",
            "中立的な状態の中で、子どもが自分をしっかりと表現し、安定した感情を保っています。",
            "不安感が大幅に和らぎ、子どもがより自由に生活する姿が見られます。",
            "恥ずかしがり屋を克服し、子どもが自信を持って友達と交流する姿にとても喜びを感じます。"
    };
    private static final String[] diaryEntries = {
            "今日は晴れた空の下で子どもと一緒に公園に行ってきました。子どもが元気に走り回って楽しそうにしている姿がとても微笑ましかったです。",
            "肌寒い朝、子どもと温かいコーヒーを飲みながら家で静かな時間を過ごしました。子どもが自分で宿題をやろうとする姿が印象的でした。",
            "今日は子どもが学校から帰ってきて、家で静かに読書をし、宿題も自分でやるなど、自立心を育む姿が誇らしかったです。",
            "子どもと一緒においしい昼食を食べて、近所を散歩しました。子どもの笑い声に一日の疲れも吹き飛びました。",
            "子どもの学校での話を聞いたところ、今日は新しい友達ができたそうです。友達と楽しそうに過ごす様子を見て嬉しくなりました。",
            "雨の日、子どもと家でパズルやお絵描きをして、ほっこりした時間を過ごしました。窓の外の雨音が心を落ち着かせてくれました。",
            "今日は子どもが家事を手伝い、積極的な姿を見せてくれました。小さな成功が積み重なっているようで、保護者として嬉しかったです。",
            "子どもと一緒に遠足の準備をしながら、期待に満ちた表情を見ました。新しい経験を楽しむ姿がとても印象的でした。",
            "子どもと一緒に簡単な料理をしてみました。材料を触りながら楽しそうにしている姿を見て、幸せな一日になりました。",
            "今日は子どもが少し疲れているように見えたので、優しい言葉をかけてゆっくり休ませてあげました。私の心も一緒に癒されました。",
            "子どもの創造的な絵や話を聞いて感動した一日でした。子どもの想像力が無限であることに本当に驚かされました。",
            "今日は子どもが遊び場で一人で遊びながら自信をつけている様子を見ました。自立心を育てる過程が素晴らしかったです。",
            "子どもと一緒に絵本を読みながら、想像の世界に浸りました。子どもの目が輝く瞬間が今日の幸せでした。",
            "今日は博物館に行ってきました。子どもが展示物に大きな関心を持ち、新しい知識を吸収する姿が印象的でした。",
            "子どもがいつもよりも元気に遊んでいました。健康的なエネルギーにあふれる姿にとても嬉しくなりました。",
            "子どもと一緒に散歩をしながら、自然の音や色を楽しみました。小さな花にも驚きを見せる子どもの純粋さが心を温かくしました。",
            "今日は子どもが自分の感情を正直に表現する姿を見て、心が一段と穏やかになりました。",
            "夕方、子どもと温かい会話を交わしながら一日の疲れを癒しました。子どもとの大切な時間が本当にかけがえのないものでした。",
            "今日は子どもがちょっとした失敗にも落ち込む様子を見ました。優しい励ましと関心で子どもを慰めてあげました。",
            "子どもと一緒に笑ったり、ときには一緒に悩んだりしながら過ごした一日でした。子どもの成長を応援し、これからもずっと愛していきます。"
    };
    private static final String[] diaryTitles = {
            "晴れた日の公園散歩",
            "温かい朝の始まり",
            "自立心が光った一日",
            "楽しいランチタイム",
            "新しい友達との出会い",
            "雨の日のぬくもり",
            "小さな成功の喜び",
            "遠足の準備",
            "料理チャレンジの楽しさ",
            "穏やかな午後の休息",
            "創造力が輝く瞬間",
            "遊び場で自信アップ",
            "想像力を刺激する物語",
            "博物館で学んだ一日",
            "元気いっぱいの時間",
            "自然とのふれあい",
            "正直な感情の表現",
            "温かい夕方の会話",
            "小さな失敗、大きな学び",
            "笑いと悩みの一日"
    };
    private static final String[] doctorOpinions = {
            "成長発達は良好です。",
            "予防接種のスケジュール確認が必要です。",
            "鉄分の摂取を推奨します。",
            "ビタミンDの摂取を推奨します。",
            "睡眠習慣の改善が必要です。",
            "定期的な歯科検診が必要です。",
            "アレルギーを引き起こす食品に注意してください。",
            "言語発達の促進が必要です。",
            "社会性の発達を促す必要があります。",
            "安全事故予防の教育が必要です。",
            "標準体重を維持しています。",
            "標準身長を維持しています。",
            "頭囲は標準範囲内です。",
            "視力は正常です。",
            "聴力は正常です。",
            "血圧は正常です。",
            "脈拍は正常です。",
            "呼吸は正常です。",
            "体温は正常です。",
            "次回の検診日程を案内します。"
    };
    private static final String[] guideTitles = {
            "iLogサービスの紹介",
            "会員登録およびログイン方法",
            "子どもの情報を登録する方法",
            "保護者と教師アカウントの連携",
            "心理アンケートの記入方法",
            "感情分析機能の使い方",
            "絵の分析機能の使い方",
            "文章の分析機能の使い方",
            "表情分析機能の使い方",
            "分析結果の確認および管理",
            "統計データの活用方法",
            "ダッシュボードの使い方",
            "権限管理の設定方法",
            "通知の設定および管理",
            "個人情報保護ポリシー",
            "データのバックアップと復元",
            "よくある問題の解決方法",
            "モバイルアプリの使い方",
            "支払いおよびサブスクリプション管理",
            "カスタマーサポートとお問い合わせ"
    };
    // 이용안내글 내용 배열 (크기 20)
    private static final String[] guideContents = {
            "iLogは、子どもの感情と発達を体系的に記録・分析し、健やかな成長を支援するサービスです。文章、絵、表情の分析を通じて心理状態を把握し、個別のソリューションを提供します。",
            "iLogサービスを利用するには会員登録が必要です。ホームページ右上の「会員登録」ボタンをクリックし、メールアドレス、パスワード、名前などの情報を入力してください。登録後、ログインしてサービスをご利用いただけます。",
            "ログイン後、「子ども管理」メニューから「子ども登録」ボタンをクリックして、子どもの名前、生年月日、性別などの基本情報を入力します。子ども情報はいつでも修正できます。",
            "教師と保護者アカウントを連携するには、「権限管理」メニューの「招待する」機能を利用してください。メールアドレスを入力して招待状を送信し、相手が承認すれば連携が完了します。",
            "心理アンケートは「心理アンケート」メニューから実施できます。子どもを選択した後、適切なアンケートの種類を選び、質問に回答してください。完了後、すぐに結果を確認できます。",
            "感情分析を行うには、「分析」メニューで「感情分析」オプションを選択してください。子どもの文章、絵、表情から分析したいタイプを選び、ファイルをアップロードするとAIが結果を提供します。",
            "絵の分析では、子どもが描いた絵をアップロードして進めます。「分析」メニューで「絵の分析」を選び、画像ファイルをアップロードしてください。色の使い方、構図、表現方法を分析し、心理状態を把握します。",
            "文章の分析では、子どもが書いた日記や手紙などのテキストを分析します。「分析」メニューで「文章の分析」を選び、テキストを入力またはアップロードしてください。単語の選択や文構造などを分析して感情状態を把握します。",
            "表情分析では、子どもの顔写真や映像を使って感情を分析します。「分析」メニューで「表情分析」を選び、写真や映像をアップロードしてください。表情を分析し、感情状態を把握します。",
            "分析結果は「結果一覧」メニューで確認できます。日付や分析タイプでフィルターをかけて結果を検索でき、各結果をクリックすると詳細と推奨ソリューションを確認できます。",
            "統計データは「統計」メニューで確認できます。期間別、分析タイプ別にフィルターをかけて、子どもの感情の変化をグラフで確認できます。CSV形式でエクスポートすることも可能です。",
            "ダッシュボードは子どもの全体的な状態を一目で把握できるページです。最近の分析結果、感情の変化、アンケート結果などが要約されて表示されます。各項目をクリックすると詳細ページに移動できます。",
            "権限の管理は「設定」メニューの「権限管理」から行えます。他のユーザー（教師・保護者など）に対して子どもの情報へのアクセス権限を付与・取り消しでき、閲覧専用や編集可能などのレベルも設定できます。",
            "通知設定は「設定」メニューの「通知管理」から行えます。新しい分析結果、アンケート完了、権限リクエストなどのイベントに関する通知を、メールやアプリのプッシュ通知で受け取ることができます。",
            "iLogはユーザーの個人情報保護を最優先に考えています。すべてのデータは暗号化されて保存され、権限のあるユーザーのみがアクセスできます。詳しくは「個人情報の取り扱い方針」をご覧ください。",
            "データのバックアップは「設定」メニューの「データ管理」で行えます。すべての分析結果とアンケートデータをZIPファイルとしてエクスポートしたり、クラウドストレージに自動バックアップするように設定できます。",
            "サービス利用中に問題が発生した場合は、「カスタマーサポート」メニューの「よくある質問」をご参照ください。ログインエラー、ファイルアップロード失敗、分析結果のエラーなど、よくある問題の解決方法を確認できます。",
            "iLogモバイルアプリはiOSおよびAndroidの両方で利用可能です。App StoreまたはGoogle Playで「iLog」を検索してインストールしてください。モバイルアプリでもウェブ版と同様の機能を使用できます。",
            "支払いおよびサブスクリプション管理は「設定」メニューの「サブスクリプション管理」で行えます。現在の契約状況の確認、プランの変更、支払い方法の管理、領収書の確認などが可能です。",
            "カスタマーサポートが必要な場合は、「カスタマーサポート」メニューの「問い合わせ」機能をご利用ください。メール（support@ilog.co.kr）または電話（02-123-4567）での問い合わせも可能です。平日9:00～18:00にはリアルタイムチャットにも対応しています。"
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
    private final PermissionRequestRepository permissionRequestRepository;
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
        MemberEntity mom = createMomAccount(random);
        createChildrenForMom(mom, random, now);

        // 추가: 교사 계정 및 교사용 자녀, 관계 생성
        initializeTeacherAccounts(mom);

        System.out.println("테스트용 기본 엔티티 저장 완료");
        System.out.printf("테스트용 부모 아이디: %s%n", this.testParentSignInId);
        System.out.printf("테스트용 교사 아이디: %s%n", this.testTeacherSignInId);
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
        String[] firstNames = {"佐藤", "鈴木"};
        String[] lastNames = {"さゆり", "ゆうじん"};
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        String fullName = "鈴木 さゆり";
        String signInId = "ldh123";
        String email = firstName.toLowerCase() + lastName.toLowerCase() + random.nextInt(100) + "@parent.com";

        MemberEntity mom = MemberEntity.builder()
                .name(fullName)
                .password(passwordEncoder.encode("a123!"))
                .signInId(signInId)
                .email(email)
                .role(MemberRole.USER)
                .relationType(RelationType.GUARDIAN)
                .personalInformationCollectionAndUsageAgreement(true)
                .build();
        this.testParentSignInId = signInId;

        MemberEntity saveMember = memberRepository.save(mom);

        initializeClaims(mom);
        return saveMember;
    }

    private void createChildrenForMom(MemberEntity mom, ThreadLocalRandom random, LocalDateTime now) {
        String[] locations = {"東京", "大阪", "札幌", "名古屋", "福岡"}; // 일본 주요 도시

        // 각 mom마다 2명의 자녀 생성
        List<FamilyBackGroundEntity> familyBackGrounds = familyBackgroundRepository.findAll();
        for (int j = 0; j < 2; j++) {
            String[] firstNames = {"佐藤", "佐藤"}; // 일본 성씨
            String[] lastNames = {"勇気", "優奈"}; // 일본식 발음의 이름 (지호/지영에 대응)
            String childName = firstNames[j] + " " + lastNames[j];
            String birthLocation = locations[j];

            ChildEntity child = ChildEntity.builder()
                    .name(childName)
                    .birthDate(now.minusYears(random.nextInt(3) + 1))
                    .birthLocation(birthLocation)
                    .note("成長に障害があります。 " + random.nextInt(100))
                    .originalProfileImgName(childName.contains("勇気") ? "jiho-0.jpg" : "jiyoung-0.jpeg")
                    .savedProfileImgName(childName.contains("勇気") ? "jiho-0.jpg" : "jiyoung-0.jpeg")
                    .gender(childName.contains("勇気") ? Gender.MAN : Gender.WOMAN)
                    .callName("ママ")
                    .build();
            childRepository.save(child);

            ChildBackGroundEntity childBackground1 = ChildBackGroundEntity.builder()
                    .familyBackGround(familyBackGrounds.get(j))
                    .child(child)
                    .build();

            ChildBackGroundEntity childBackground2 = ChildBackGroundEntity.builder()
                    .familyBackGround(familyBackGrounds.get(j + 2))
                    .child(child)
                    .build();

            List<ChildBackGroundEntity> childBackGrounds = List.of(childBackground1, childBackground2);
            child.replaceAllChildBackGrounds(childBackGrounds);

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
                        new QuestionResponse(questions1.get(3).getItem(), questions1.get(3).getExample(), random.nextInt(1, 6))
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
                        new QuestionResponse(questions2.get(3).getItem(), questions2.get(3).getExample(), random.nextInt(1, 6))
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
                        new QuestionResponse(questions3.get(3).getItem(), questions3.get(3).getExample(), random.nextInt(1, 6))
                );
                int section3Score = qResponses3.stream().mapToInt(QuestionResponse::getScore).sum();
                sectionResponses.add(new SectionResponse(section3.getTitle(), qResponses3, section3Score));

                int totalScore = sectionResponses.stream().mapToInt(SectionResponse::getSectionLikertScore).sum();
                String diagnosisComment;
                if (totalScore < 15) {
                    diagnosisComment = "危険";
                } else if (totalScore < 30) {
                    diagnosisComment = "注意必要";
                } else if (totalScore < 45) {
                    diagnosisComment = "正常";
                } else {
                    diagnosisComment = "良";
                }
                String comment = "心理評価の結果 " +
                        totalScore +
                        " " +
                        diagnosisComment;

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
                    .latitude(LATITUDES[k])
                    .longitude(LONGITUDES[k])
                    .locationName(LOCATION_NAMES[k])
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
                    .weatherDesc("今日の天気は, " + (random.nextBoolean() ? "晴れ" : "曇り"))
                    .build();
            target.setWeather(weather);

            AnalysisResultEntity analysisResult = AnalysisResultEntity.builder()
                    .emotionScore(random.nextDouble())
                    .analysisTarget(target)
                    .title(analysisResultTitles[k])
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
                    .originalFileName(String.format("test-healthCheck-%d.jpeg", k % 4))
                    .savedFileName(String.format("test-healthCheck-%d.jpeg", k % 4))
                    .build();
            childHealthCheckRepository.save(healthCheck);
        }
    }

    private void initializeClaims(MemberEntity member) {
        // 각 mom 당 한 건의 Claim 및 ClaimAnswer 생성
        // 이 메서드는 필요에 따라 별도로 분리할 수 있습니다.
        MemberEntity admin = memberRepository.findBySignInId("ADMIN").orElseThrow(() -> new MemberNotFoundException("ADMIN"));
        ClaimEntity claim1 = ClaimEntity.builder()
                .author(member)
                .title("感情分析はどのように動作しますか？")
                .content("感情分析システムの仕組みを知りたいです。")
                .type(ClaimType.USAGE)
                .build();
        claimRepository.save(claim1);

        ClaimEntity claim2 = ClaimEntity.builder()
                .author(member)
                .title("正確な天気はどのように取得されますか？")
                .content("天気取得の仕組みを知りたいです。")
                .type(ClaimType.USAGE)
                .build();
        claimRepository.save(claim2);

        ClaimAnswerEntity claimAnswer = ClaimAnswerEntity.builder()
                .author(admin)
                .claim(claim2)
                .content("天気はOpenWeatherAPIの情報に基づいて取得されています。")
                .build();
        claimAnswerRepository.save(claimAnswer);
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

    private void initializeTeacherAccounts(MemberEntity requester) {
        // 교사 계정 생성
        MemberEntity teacher = MemberEntity.builder()
                .signInId("kim123")
                .password(passwordEncoder.encode("a123!"))
                .name("高橋先生")
                .email("bldolphin96@gmail.com")
                .relationType(RelationType.TEACHER)
                .personalInformationCollectionAndUsageAgreement(true)
                .build();
        memberRepository.save(teacher);
        this.testTeacherSignInId = "kim123";

        // 기존에 저장된 아동 중 하나를 선택하여 교사와 관계 생성
        List<ChildEntity> existingChildren = childRepository.findAll();
        // 예시로 첫 번째 아동을 선택 (필요에 따라 다른 조건을 적용할 수 있음)
        RelationShipEntity teacherChildRelation = RelationShipEntity.builder()
                .member(teacher)
                .child(existingChildren.get(0))
                .permissionLevel(PermissionLevel.VIEWER)
                .relationType(RelationType.TEACHER)
                .build();
        relationShipRepository.save(teacherChildRelation);

        PermissionRequestEntity permissionRequest = PermissionRequestEntity.builder()
                .alias("幼稚園先生")
                .child(existingChildren.get(0))
                .invitee(teacher)
                .relationType(RelationType.TEACHER)
                .permissionStatus(PermissionRequestStatus.ACCEPTED)
                .requestLinkCode("sdfsdf")
                .requester(requester)
                .build();

        permissionRequestRepository.save(permissionRequest);
        ThreadLocalRandom current = ThreadLocalRandom.current();
//        createMentalSurveyResponses(existingChildren.get(0), teacher, current);
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