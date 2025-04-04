# iLog

**AI ベースの子ども向け日記帳ウェブアプリケーション**

iLog は、子どもの文章、絵、音声、写真などを記録し、AI による感情・心理分析を通じて、保護者や教育者に有益なソリューションを提供するウェブサービスです。  
親、教師、保育士など子どもに関わる複数のユーザーが、同じ子どものデータを共有・管理することができます。

---

## ✨ 主な機能

### 👤 ユーザー関連機能
- 会員登録、ログイン、パスワード再発行、マイページ
- 多言語ランディングページ、利用規約、プライバシーポリシー確認
- お問い合わせセンターからの質問および返信確認

### 📊 ダッシュボード
- 保護者向け・教師向けのダッシュボード
- 最新の心理アンケート結果と感情分析をグラフで確認可能

### 🌱 成長記録
- 子どものプロフィールや身体情報の登録・管理
- 日記の作成・修正・削除
- 年齢別心理アンケートと統計表示

### 🤖 AI 感情分析
- 写真や絵をアップロードして感情分析を実行
- 分析結果に基づいた対応ガイドの提供
- メモ・満足度保存、再分析リクエスト機能

### 🔐 権限管理
- 閲覧権限の招待・承認・削除機能

---

## 🧑‍🎓 主な対象ユーザー
- 幼児・児童の保護者（親、家族）
- 保育従事者（保育園・幼稚園の先生）
- 教育・福祉機関の関係者

---

## 🛠️ 使用技術スタック

![Java](https://img.shields.io/badge/Java-17.0.11-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.2-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring_Security-Yes-blue)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-Yes-blue)
![MongoDB](https://img.shields.io/badge/MongoDB-7.x-green)
![MySQL](https://img.shields.io/badge/MySQL-8.3.1-blue)
![H2](https://img.shields.io/badge/H2_DB-Yes-lightgrey)
![Gradle](https://img.shields.io/badge/Gradle-8.9-02303A)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Yes-yellowgreen)
![Bulma](https://img.shields.io/badge/Bulma_CSS-Yes-lightblue)
![jQuery](https://img.shields.io/badge/jQuery-3.7.1-blue)
![SweetAlert2](https://img.shields.io/badge/SweetAlert--2-Yes-pink)
![FilePond](https://img.shields.io/badge/FilePond-Yes-lightgreen)
![HighChart.js](https://img.shields.io/badge/HighChart.js-Yes-yellow)
![OpenAI GPT-4](https://img.shields.io/badge/GPT--4_Chat%2FVISION-Yes-critical)
![OpenWeather](https://img.shields.io/badge/OpenWeather_API-Yes-blue)
![Naver Maps](https://img.shields.io/badge/Naver_Maps_API-Yes-green)

---

## 🗂️ データベース構成概要

### 🐬 RDB (MySQL)
- `member`, `child`, `child_diary`, `analysis_result`, `claim`, `guide`, `relationship` など

### EntityRelationshipDiagram
![ilog-erd.png](/readme-images/ilog-erd.png)

# 📦 リレーショナルデータベーステーブル構成 (MySQL)

### `member` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| member_id | BIGINT | ✅ | ✅ |  | 사용자 ID |
| email | VARCHAR(255) | ✅ |  |  | 이메일 주소 |
| name | VARCHAR(100) |  |  |  | 사용자 이름 |
| sign_in_id | VARCHAR(100) |  |  |  | 로그인 ID |
| password | VARCHAR(65) |  |  |  | 비밀번호 |
| relation_type | ENUM |  |  |  | 관계 유형 |
| role | ENUM | ✅ |  |  | 사용자 권한 |

---

### `analysis_result` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| result_id | BIGINT | ✅ | ✅ |  | 감정 분석 결과 ID |
| child_id | BIGINT | ✅ |  | ✅ | 아동 ID |
| result | TEXT |  |  |  | 분석 결과 |

---

### `analysis_result_note` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| note_id | BIGINT | ✅ | ✅ |  | 메모 ID |
| result_id | BIGINT | ✅ |  | ✅ | 분석 결과 ID |
| note | TEXT |  |  |  | 추가 메모 |

---

### `analysis_satisfaction` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| satisfaction_id | BIGINT | ✅ | ✅ |  | 만족도 ID |
| result_id | BIGINT | ✅ |  | ✅ | 분석 결과 ID |
| score | INT |  |  |  | 만족도 점수 |

---

### `analysis_target` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| target_id | BIGINT | ✅ | ✅ |  | 분석 대상 ID |
| file_path | VARCHAR(255) | ✅ |  |  | 파일 경로 |

---

### `analysis_target_type` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| target_id | BIGINT | ✅ | ✅ | ✅ | 분석 대상 ID |
| type_id | BIGINT | ✅ | ✅ | ✅ | 분석 유형 ID |

---

### `analysis_type` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| type_id | BIGINT | ✅ | ✅ |  | 분석 유형 ID |
| name | VARCHAR(100) | ✅ |  |  | 유형 이름 |

---

### `child` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| child_id | BIGINT | ✅ | ✅ |  | 아동 ID |
| name | VARCHAR(100) | ✅ |  |  | 이름 |
| birth_date | DATETIME |  |  |  | 생일 |
| gender | ENUM | ✅ |  |  | 성별 |

---

### `child_background` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| child_id | BIGINT | ✅ | ✅ | ✅ | 아동 ID |
| background_id | BIGINT | ✅ | ✅ | ✅ | 가족 배경 ID |

---

### `child_diary` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| diary_id | BIGINT | ✅ | ✅ |  | 일기 ID |
| child_id | BIGINT | ✅ |  | ✅ | 아동 ID |
| content | TEXT | ✅ |  |  | 내용 |

---

### `child_record` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| record_id | BIGINT | ✅ | ✅ |  | 기록 ID |
| child_id | BIGINT | ✅ |  | ✅ | 아동 ID |
| height | FLOAT |  |  |  | 키 |
| weight | FLOAT |  |  |  | 몸무게 |

---

### `claim` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| claim_id | BIGINT | ✅ | ✅ |  | 문의 ID |
| member_id | BIGINT | ✅ |  | ✅ | 회원 ID |
| message | TEXT | ✅ |  |  | 문의 내용 |

---

### `claim_answer` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| answer_id | BIGINT | ✅ | ✅ |  | 답변 ID |
| claim_id | BIGINT | ✅ |  | ✅ | 문의 ID |
| response | TEXT | ✅ |  |  | 답변 내용 |

---

### `family_background` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| background_id | BIGINT | ✅ | ✅ |  | 배경 ID |
| type | ENUM | ✅ |  |  | 가족 유형 |

---

### `guide` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| guide_id | BIGINT | ✅ | ✅ |  | 가이드 ID |
| title | VARCHAR(255) | ✅ |  |  | 제목 |
| content | TEXT | ✅ |  |  | 내용 |

---

### `health_check` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| check_id | BIGINT | ✅ | ✅ |  | 건강검진 ID |
| child_id | BIGINT | ✅ |  | ✅ | 아동 ID |
| file_path | VARCHAR(255) | ✅ |  |  | 파일 경로 |

---

### `permission_request` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| request_id | BIGINT | ✅ | ✅ |  | 요청 ID |
| child_id | BIGINT | ✅ |  | ✅ | 아동 ID |
| requester_id | BIGINT | ✅ |  | ✅ | 요청자 ID |

---

### `relationship` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| relationship_id | BIGINT | ✅ | ✅ |  | 관계 ID |
| member_id | BIGINT | ✅ |  | ✅ | 회원 ID |
| child_id | BIGINT | ✅ |  | ✅ | 아동 ID |
| role | ENUM | ✅ |  |  | 역할 |

---

### `weather` テーブル

| カラム名 | データ型 | Not Null | PK | FK | 説明 |
|--------|--------------|-----------|----|----|------|
| weather_id | BIGINT | ✅ | ✅ |  | 날씨 정보 ID |
| date_time | DATETIME | ✅ |  |  | 기록 시각 |
| location | VARCHAR(255) | ✅ |  |  | 위치 |

---



### 🍃 NoSQL (MongoDB)
- `MentalSurveys`：アンケート定義
- `MentalSurveyResponses`：回答結果の保存

### EntityRelationshipDiagram
![ilog-noSql-erd.png](/readme-images/ilog-noSql-erd.png)

### Collections

**MentalSurveys**
```json
{
  "_id": ObjectId, -> 엔티티의 기본 키
  "id": String, -> 심리 설문의 시맨틱 아이디
  "title": String, -> 심리 설문의 제목
  "type": String, -> 심리 설문의 종류(보호자용, 선생님용)
  "sections": Array [ -> 심리 설문의 항목의 배열
    {
      "title": String, -> 각 항목의 제목
      "questions": Array [ -> 각 항목이 지닌 질문의 배열
        {
          "item": String, -> 각 질문의 제목
          "example": String -> 각 질문의 예시(짧은 설명)
        },
      ]
    },
  ],
  "createdAt": Date, -> 생성 일시
  "description": String -> 심리 설문의 설명
}
```

**MentalSurveyResponses**
```json
{
  "_id": ObjectId, -> 엔티티의 기본 키
  "surveyTitle": String, -> 심리 설문의 제목
  "surveyId": String, -> 심리 설문의 시맨틱 아이디
  "childId": Int64, -> 해당 아동의 식별자(기본 키)
  "respondentId": Int64, -> 응답자의 식별자(기본 키)
  "relationType": String, -> 설문의 종류(보호자, 선생님)
  "sectionResponses": Array [ -> 설문의 각 항목에 해당하는 응답의 배열
    {
      "sectionTitle": String, -> 각 항목의 제목
      "questionResponses": Array [ -> 각 항목이 지닌 질문에 대한 응답의 배열
        {
          "questionItem": String, -> 각 질문의 제목
          "example": String, -> 각 질문의 예시(짧은 설명)
          "score": Int32 -> 응답 점수
        },
      ],
      "sectionLikertScore": Int32 -> 각 항목의 점수 총합
    },
  ],
  "totalLikertScore": Int32, -> 전체 설문의 점수 총합
  "comment": String, -> 응답에 대한 진단 결과
  "createdAt": Date, -> 생성 일시
  "lastModifiedAt": Date, -> 수정 이릿
  "_class": String -> 관련된 엔티티 클래스의 메타데이터
}

```
---

## 🎨 デザイン

- **ロゴの意味**: “i” は子ども（アイ）、“Log” は日記の意味
- **カラースキーム**:  
  - Primary: `#fdf5bf`  
  - Secondary: `#bee6d9`

---

## 🧑‍🤝‍🧑 チーム紹介

| 名前     | 担当 |
|----------|------|
| 音ホジュン（リーダー） | プロジェクト管理、AI 機能、DB 設計 |
| キム・ボギョン   | UI/UX デザイン、子ども情報機能開発 |
| キム・ウンジン   | 企画、UX 設計、心理アンケート機能開発 |
| イ・ドフン   | 日記帳、感情統計、権限管理機能開発 |
| チョン・ジェファン   | お問い合わせ、身体情報、会員機能開発 |
| チョン・ジュンソン   | 身体情報、心理統計、権限管理機能開発 |

---
