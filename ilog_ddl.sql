-- Drop existing tables (순서에 주의)
DROP TABLE IF EXISTS weather;
DROP TABLE IF EXISTS relationship;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS health_check;
DROP TABLE IF EXISTS guide;
DROP TABLE IF EXISTS claim_answer;
DROP TABLE IF EXISTS claim;
DROP TABLE IF EXISTS child_record;
DROP TABLE IF EXISTS child_diary;
DROP TABLE IF EXISTS child;
DROP TABLE IF EXISTS analysis_target;
DROP TABLE IF EXISTS analysis_result_note;
DROP TABLE IF EXISTS analysis_result;

-- =====================================================
-- Table: analysis_result
-- =====================================================
CREATE TABLE analysis_result (
    emotion_score FLOAT(53),
    analysis_id BIGINT AUTO_INCREMENT,
    child_asset_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    analysis_result VARCHAR(1000),
    suggested_solution VARCHAR(1000),
    emotion_type ENUM('ANGRY','ANXIOUS','BORED','CONFUSED','EXCITED','FEAR','HAPPY','NEUTRAL','SAD','SHY','SURPRISED'),
    PRIMARY KEY (analysis_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: analysis_result_note
-- =====================================================
CREATE TABLE analysis_result_note (
    satisfaction_level INTEGER,
    analysis_result_id BIGINT NOT NULL,
    analysis_result_note_id BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP(6) NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    content VARCHAR(1000) NOT NULL,
    PRIMARY KEY (analysis_result_note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: analysis_target
-- =====================================================
CREATE TABLE analysis_target (
    child_asset_id BIGINT AUTO_INCREMENT,
    child_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    register_date TIMESTAMP(6) NULL,
    uploaded_by BIGINT,
    original_survey_file_name VARCHAR(200),
    saved_survey_file_name VARCHAR(200),
    supplement VARCHAR(1000),
    companion VARCHAR(255),
    type ENUM('DRAWING','PHOTO','VOICE','WRITING') NOT NULL,
    PRIMARY KEY (child_asset_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: child
-- =====================================================
CREATE TABLE child (
    birth_date TIMESTAMP(6) NULL,
    child_id BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP(6) NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    name VARCHAR(100) NOT NULL,
    birth_location VARCHAR(255),
    note VARCHAR(255),
    original_profile_img_name VARCHAR(255),
    saved_profile_img_name VARCHAR(255),
    gender ENUM('MAN','NONE','WOMAN') NOT NULL,
    PRIMARY KEY (child_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: child_diary
-- =====================================================
CREATE TABLE child_diary (
    author_id BIGINT NOT NULL,
    child_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    diary_id BIGINT AUTO_INCREMENT,
    modified_at TIMESTAMP(6) NULL,
    content TEXT NOT NULL,
    title VARCHAR(255),
    PRIMARY KEY (diary_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: child_record
-- =====================================================
CREATE TABLE child_record (
    height FLOAT(53),
    left_eye FLOAT(53),
    right_eye FLOAT(53),
    weight FLOAT(53),
    child_id BIGINT NOT NULL,
    child_record_id BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP(6) NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    register_date TIMESTAMP(6) NULL,
    note VARCHAR(1000),
    PRIMARY KEY (child_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: claim
-- =====================================================
CREATE TABLE claim (
    claim_id BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP(6) NOT NULL,
    member_id BIGINT NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    type ENUM('GENERAL','PRIVACY','USAGE') NOT NULL,
    PRIMARY KEY (claim_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: claim_answer
-- =====================================================
CREATE TABLE claim_answer (
    claim_answer_id BIGINT AUTO_INCREMENT,
    claim_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    member_id BIGINT NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    PRIMARY KEY (claim_answer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: guide
-- =====================================================
CREATE TABLE guide (
    created_at TIMESTAMP(6) NOT NULL,
    guide_id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    PRIMARY KEY (guide_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: health_check
-- =====================================================
CREATE TABLE health_check (
    child_id BIGINT NOT NULL,
    child_record_id BIGINT UNIQUE,
    created_at TIMESTAMP(6) NOT NULL,
    health_survey_id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    original_file_name VARCHAR(200),
    saved_file_name VARCHAR(200),
    PRIMARY KEY (health_survey_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: member
-- =====================================================
CREATE TABLE member (
    relation_type TINYINT,
    CHECK (relation_type BETWEEN 0 AND 4),
    created_at TIMESTAMP(6) NOT NULL,
    member_id BIGINT AUTO_INCREMENT,
    modified_at TIMESTAMP(6) NULL,
    password VARCHAR(65) NOT NULL,
    name VARCHAR(100) NOT NULL,
    sign_in_id VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    role ENUM('ADMIN','USER') NOT NULL,
    PRIMARY KEY (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: relationship
-- =====================================================
CREATE TABLE relationship (
    child_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    member_id BIGINT NOT NULL,
    modified_at TIMESTAMP(6) NULL,
    relationship_id BIGINT AUTO_INCREMENT,
    permission_level ENUM('EDITOR','OWNER','VIEWER') NOT NULL,
    relation_type ENUM('CARER','EXPERT','GUARDIAN','PARENT','TEACHER') NOT NULL,
    PRIMARY KEY (relationship_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: weather
-- =====================================================
CREATE TABLE weather (
    humidity INTEGER,
    temperature FLOAT(24),
    wind_speed FLOAT(24),
    analysis_target_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP(6) NOT NULL,
    id BIGINT AUTO_INCREMENT,
    modified_at TIMESTAMP(6) NULL,
    recorded_at TIMESTAMP(6) NULL,
    weather_desc VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Foreign Key Constraints
-- =====================================================

ALTER TABLE analysis_result
    ADD CONSTRAINT FKxux3i8j6q0vkcbdxn6i4siq
    FOREIGN KEY (child_asset_id) REFERENCES analysis_target(child_asset_id);

ALTER TABLE analysis_result_note
    ADD CONSTRAINT FK3q8mvsrhm6p0whqkju6p2p28y
    FOREIGN KEY (analysis_result_id) REFERENCES analysis_result(analysis_id);

ALTER TABLE analysis_target
    ADD CONSTRAINT FKk01143xtkclu77ykl11orsy51
    FOREIGN KEY (child_id) REFERENCES child(child_id);

ALTER TABLE analysis_target
    ADD CONSTRAINT FKqp3jjvj7yfnnts0x8ehd5tjdx
    FOREIGN KEY (uploaded_by) REFERENCES member(member_id);

ALTER TABLE child_diary
    ADD CONSTRAINT FKaamyj8p0iblu1cvv11labdhov
    FOREIGN KEY (author_id) REFERENCES member(member_id);

ALTER TABLE child_diary
    ADD CONSTRAINT FKlx3ifvsd0i205vw95m8nhd24d
    FOREIGN KEY (child_id) REFERENCES child(child_id);

ALTER TABLE child_record
    ADD CONSTRAINT FKnls6q1ddvvmb8qnb340ep3ojs
    FOREIGN KEY (child_id) REFERENCES child(child_id);

ALTER TABLE claim
    ADD CONSTRAINT FKb1f4qnsth2py6jn2ufqp2wqb6
    FOREIGN KEY (member_id) REFERENCES member(member_id);

ALTER TABLE claim_answer
    ADD CONSTRAINT FK13r8x5c40wk3nt6yow3rae7a8
    FOREIGN KEY (member_id) REFERENCES member(member_id);

ALTER TABLE claim_answer
    ADD CONSTRAINT FKp7o80t72w63ep039d2esmi8dx
    FOREIGN KEY (claim_id) REFERENCES claim(claim_id);

ALTER TABLE guide
    ADD CONSTRAINT FKr77ccissr4xmfoga75dx0dna3
    FOREIGN KEY (member_id) REFERENCES member(member_id);

ALTER TABLE health_check
    ADD CONSTRAINT FK5m10eckmd7ly2yrf27umbbf76
    FOREIGN KEY (child_id) REFERENCES child(child_id);

ALTER TABLE health_check
    ADD CONSTRAINT FK9xbdt5yeg0ukjbyffmxka8p41
    FOREIGN KEY (child_record_id) REFERENCES child_record(child_record_id);

ALTER TABLE health_check
    ADD CONSTRAINT FKceca8q54cor1q1qbkbcg6rwj9
    FOREIGN KEY (member_id) REFERENCES member(member_id);

ALTER TABLE relationship
    ADD CONSTRAINT FKgbi5415as44qw0a0h8hre11c4
    FOREIGN KEY (child_id) REFERENCES child(child_id);

ALTER TABLE relationship
    ADD CONSTRAINT FKa5kgr9ciiwc2ubu1vt640nqij
    FOREIGN KEY (member_id) REFERENCES member(member_id);

ALTER TABLE weather
    ADD CONSTRAINT FKicmak91mjnfh8t3ksm1ohem7i
    FOREIGN KEY (analysis_target_id) REFERENCES analysis_target(child_asset_id);
