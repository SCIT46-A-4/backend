create database ilog;
use ilog;

-- Drop tables (역순으로 삭제하여 외래키 제약 조건 문제 회피)
DROP TABLE IF EXISTS analysis_result_note;
DROP TABLE IF EXISTS analysis_result;
DROP TABLE IF EXISTS child_diary;
DROP TABLE IF EXISTS child_record;
DROP TABLE IF EXISTS health_survey;
DROP TABLE IF EXISTS relationship;
DROP TABLE IF EXISTS child_asset;
DROP TABLE IF EXISTS child;
DROP TABLE IF EXISTS claim_answer;
DROP TABLE IF EXISTS claim;
DROP TABLE IF EXISTS help;
DROP TABLE IF EXISTS mental_answer;
DROP TABLE IF EXISTS mental_option;
DROP TABLE IF EXISTS mental_question;
DROP TABLE IF EXISTS mental_response;
DROP TABLE IF EXISTS mental_survey;
DROP TABLE IF EXISTS member;

-- =====================================================
-- Table: member
-- =====================================================
CREATE TABLE member (
		member_id BIGINT AUTO_INCREMENT,
		userid VARCHAR(100) NOT NULL,
    password VARCHAR(65) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role ENUM('ADMIN','USER'),
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: child
-- =====================================================
CREATE TABLE child (
    child_id BIGINT AUTO_INCREMENT,
    birth_date TIMESTAMP,
    name VARCHAR(100) NOT NULL,
    note VARCHAR(255),
    gender ENUM('MAN','NONE','WOMAN') NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (child_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: child_asset
-- =====================================================
CREATE TABLE child_asset (
    child_id BIGINT NOT NULL,
    child_asset_id BIGINT AUTO_INCREMENT,
    uploaded_by BIGINT,
    type ENUM('DRAWING','PHOTO','VOICE','WRITING') NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,

    PRIMARY KEY (child_asset_id),
    CONSTRAINT fk_child_asset_child FOREIGN KEY (child_id)
        REFERENCES child(child_id) ON DELETE CASCADE,
    CONSTRAINT fk_child_asset_uploaded_by FOREIGN KEY (uploaded_by)
        REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: analysis_result
-- =====================================================
CREATE TABLE analysis_result (
    child_asset_id BIGINT NOT NULL,
    analysis_id BIGINT AUTO_INCREMENT,
    emotion_score FLOAT,
    analysis_result VARCHAR(1000),
    suggested_solution VARCHAR(1000),
    created_at TIMESTAMP  NOT NULL default current_timestamp,
    modified_at TIMESTAMP,

    PRIMARY KEY (analysis_id),
    CONSTRAINT fk_analysis_result_child_asset FOREIGN KEY (child_asset_id)
        REFERENCES child_asset(child_asset_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: analysis_result_note
-- =====================================================
CREATE TABLE analysis_result_note (
    analysis_result_id BIGINT NOT NULL,
    analysis_result_note_id BIGINT AUTO_INCREMENT,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (analysis_result_note_id),
    CONSTRAINT fk_analysis_result_note FOREIGN KEY (analysis_result_id)
        REFERENCES analysis_result(analysis_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: child_diary
-- =====================================================
CREATE TABLE child_diary (
    diary_id BIGINT AUTO_INCREMENT,
    author_id BIGINT NOT NULL,
    child_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP,
    PRIMARY KEY (diary_id),
    CONSTRAINT fk_child_diary_author FOREIGN KEY (author_id)
        REFERENCES member(member_id),
    CONSTRAINT fk_child_diary_child FOREIGN KEY (child_id)
        REFERENCES child(child_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: child_record
-- =====================================================
CREATE TABLE child_record (
    child_record_id BIGINT AUTO_INCREMENT,
    height FLOAT,
    left_eye FLOAT,
    right_eye FLOAT,
    weight FLOAT,
    child_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    name VARCHAR(100) NOT NULL,
    original_photo_name VARCHAR(100),
    saved_photo_name VARCHAR(100),
    note VARCHAR(1000),
    gender ENUM('MAN','NONE','WOMAN') NOT NULL,
    PRIMARY KEY (child_record_id),
    CONSTRAINT fk_child_record_child FOREIGN KEY (child_id)
        REFERENCES child(child_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: health_survey
-- =====================================================
CREATE TABLE health_survey (
    health_survey_id BIGINT AUTO_INCREMENT,
    child_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    original_survey_file_name VARCHAR(200),
    saved_survey_file_name VARCHAR(200),
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (health_survey_id),
    CONSTRAINT fk_health_survey_child FOREIGN KEY (child_id)
        REFERENCES child(child_id) ON DELETE CASCADE,
    CONSTRAINT fk_health_survey_member FOREIGN KEY (member_id)
        REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: relationship
-- =====================================================
CREATE TABLE relationship (
    relationship_id BIGINT AUTO_INCREMENT,
    child_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    permission_level ENUM('EDITOR','OWNER','VIEWER') NOT NULL,
    relation_type ENUM('CARER','EXPERT','PARENT','TEACHER') NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (relationship_id),
    CONSTRAINT fk_relationship_child FOREIGN KEY (child_id)
        REFERENCES child(child_id) ON DELETE CASCADE,
    CONSTRAINT fk_relationship_member FOREIGN KEY (member_id)
        REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: claim
-- =====================================================
CREATE TABLE claim (
    claim_id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    type ENUM('GENERAL','PRIVACY','USAGE') NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP(6),

    PRIMARY KEY (claim_id),
    CONSTRAINT fk_claim_member FOREIGN KEY (member_id)
        REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: claim_answer
-- =====================================================
CREATE TABLE claim_answer (
    claim_answer_id BIGINT AUTO_INCREMENT,
    claim_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (claim_answer_id),
    CONSTRAINT fk_claim_answer_member FOREIGN KEY (member_id)
        REFERENCES member(member_id),
    CONSTRAINT fk_claim_answer_claim FOREIGN KEY (claim_id)
        REFERENCES claim(claim_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: help
-- =====================================================
CREATE TABLE help (
    help_id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (help_id),
    CONSTRAINT fk_help_member FOREIGN KEY (member_id)
        REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: mental_survey
-- =====================================================
CREATE TABLE mental_survey (
    mental_survey_id BIGINT AUTO_INCREMENT,
    title VARCHAR(100),
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (mental_survey_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: mental_question
-- =====================================================
CREATE TABLE mental_question (
    mental_question_id BIGINT AUTO_INCREMENT,
    is_required BOOLEAN NOT NULL,
    sort_order INTEGER NOT NULL,
    mental_survey_id BIGINT NOT NULL,
    question_text VARCHAR(200) NOT NULL,
    question_type ENUM('BOOLEAN','MULTIPLE_CHOICE','RATING','TEXT') NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (mental_question_id),
    CONSTRAINT fk_mental_question_survey FOREIGN KEY (mental_survey_id)
        REFERENCES mental_survey(mental_survey_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: mental_option
-- =====================================================
CREATE TABLE mental_option (
    mental_option_id BIGINT AUTO_INCREMENT,
    sort_order INTEGER,
    mental_question_id BIGINT NOT NULL,
    option_text VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,

    PRIMARY KEY (mental_option_id),
    CONSTRAINT fk_mental_option_question FOREIGN KEY (mental_question_id)
        REFERENCES mental_question(mental_question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: mental_response
-- =====================================================
CREATE TABLE mental_response (
    mental_response_id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    survey_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (mental_response_id),
    CONSTRAINT fk_mental_response_member FOREIGN KEY (member_id)
        REFERENCES member(member_id),
    CONSTRAINT fk_mental_response_survey FOREIGN KEY (survey_id)
        REFERENCES mental_survey(mental_survey_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: mental_answer
-- =====================================================
CREATE TABLE mental_answer (
    mental_answer_id BIGINT AUTO_INCREMENT,
    mental_option_id BIGINT,
    mental_question_id BIGINT NOT NULL,
    mental_response_id BIGINT NOT NULL,
    answer VARCHAR(1000),
    created_at TIMESTAMP NOT NULL default current_timestamp,
    modified_at TIMESTAMP,
    PRIMARY KEY (mental_answer_id),
    CONSTRAINT fk_mental_answer_question FOREIGN KEY (mental_question_id)
        REFERENCES mental_question(mental_question_id),
    CONSTRAINT fk_mental_answer_option FOREIGN KEY (mental_option_id)
        REFERENCES mental_option(mental_option_id),
    CONSTRAINT fk_mental_answer_response FOREIGN KEY (mental_response_id)
        REFERENCES mental_response(mental_response_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
