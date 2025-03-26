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

CREATE TABLE analysis_result (
  analysis_result_id BIGINT AUTO_INCREMENT,
  emotion_score DOUBLE,
  analysis_result_text VARCHAR(1000),
  suggested_solution VARCHAR(1000),
  title VARCHAR(255),
  emotion_type ENUM('ANGRY','ANXIOUS','BORED','CONFUSED','EXCITED','FEAR','HAPPY','NEUTRAL','SAD','SHY','SURPRISED'),
  analysis_target_id BIGINT NOT NULL,  -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (analysis_result_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE analysis_result_note (
  analysis_result_note_id BIGINT AUTO_INCREMENT,
  content VARCHAR(1000) NOT NULL,
  analysis_result_id BIGINT NOT NULL UNIQUE,  -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (analysis_result_note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE analysis_satisfaction (
  analysis_satisfaction_id BIGINT AUTO_INCREMENT,
  satisfaction_score INT,
  analysis_result_id BIGINT UNIQUE,  -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (analysis_satisfaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE analysis_target (
  analysis_target_id BIGINT AUTO_INCREMENT,
  latitude DOUBLE,
  longitude DOUBLE,
  supplement VARCHAR(1000),
  analyzed_text VARCHAR(255),
  companion VARCHAR(255),
  location_name VARCHAR(255),
  child_id BIGINT NOT NULL,  -- 외래키
  uploaded_by BIGINT,        -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  register_date DATETIME(6),
  original_target_file_name VARCHAR(200),  -- 파일 관련
  saved_target_file_name VARCHAR(200),       -- 파일 관련
  PRIMARY KEY (analysis_target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE analysis_target_type (
  analysis_target_type_id BIGINT AUTO_INCREMENT,
  analysis_target_id BIGINT,  -- 외래키
  analysis_type_id BIGINT,    -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (analysis_target_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE analysis_type (
  analysis_type_id BIGINT AUTO_INCREMENT,
  type ENUM('DRAWING','PHOTO','WRITING'),
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (analysis_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE child (
  child_id BIGINT AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  birth_location VARCHAR(255),
  callname VARCHAR(255),
  note VARCHAR(255),
  gender ENUM('MAN','NONE','WOMAN') NOT NULL,
  original_profile_img_name VARCHAR(255),  -- 파일 관련
  saved_profile_img_name VARCHAR(255),       -- 파일 관련
  birth_date DATETIME(6),
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (child_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE child_background (
  child_background_id BIGINT AUTO_INCREMENT,
  child_id BIGINT,               -- 외래키
  family_background_id BIGINT,   -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (child_background_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE child_diary (
  diary_id BIGINT AUTO_INCREMENT,
  content TEXT NOT NULL,
  title VARCHAR(255),
  author_id BIGINT,      -- 외래키
  child_id BIGINT NOT NULL,  -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (diary_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE child_record (
  child_record_id BIGINT AUTO_INCREMENT,
  height DOUBLE,
  left_eye DOUBLE,
  right_eye DOUBLE,
  weight DOUBLE,
  note VARCHAR(1000),
  child_id BIGINT NOT NULL,  -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  register_date DATETIME(6),
  PRIMARY KEY (child_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE claim (
  claim_id BIGINT AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  type ENUM('GENERAL','PRIVACY','USAGE') NOT NULL,
  member_id BIGINT NOT NULL,  -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (claim_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE claim_answer (
  claim_answer_id BIGINT AUTO_INCREMENT,
  content VARCHAR(1000) NOT NULL,
  claim_id BIGINT NOT NULL,    -- 외래키
  member_id BIGINT NOT NULL,   -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (claim_answer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE family_background (
  family_background_id BIGINT AUTO_INCREMENT,
  family_background ENUM('COUNSELING_EXPERIENCE','DUAL_INCOME','GRANDPARENTING','MULTICULTURAL','ONLY_CHILD','SIBLINGS','SINGLE_INCOME_DAD','SINGLE_INCOME_MOM','SINGLE_PARENT','SPECIAL_NEEDS'),
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (family_background_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE guide (
  guide_id BIGINT AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  member_id BIGINT NOT NULL,  -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (guide_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE health_check (
  health_check_id BIGINT AUTO_INCREMENT,
  child_id BIGINT NOT NULL,         -- 외래키
  child_record_id BIGINT UNIQUE,      -- 외래키
  member_id BIGINT,                   -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  original_file_name VARCHAR(200),    -- 파일 관련
  saved_file_name VARCHAR(200),         -- 파일 관련
  PRIMARY KEY (health_check_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE member (
  member_id BIGINT AUTO_INCREMENT,
  password VARCHAR(65),
  name VARCHAR(100),
  sign_in_id VARCHAR(100) UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  personal_information_collection_and_usage_agreement BOOLEAN NOT NULL,
  relation_type ENUM('ADMIN','CARER','GUARDIAN','PARENT','TEACHER'),
  role ENUM('ADMIN','LEAVED','USER') NOT NULL,
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE permission_request (
  permission_request_id BIGINT AUTO_INCREMENT,
  alias VARCHAR(255),
  request_link_code VARCHAR(255),
  requested_relation_type ENUM('ADMIN','CARER','GUARDIAN','PARENT','TEACHER') NOT NULL,
  status ENUM('ABORTED','ACCEPTED','EXPIRED','PENDING') NOT NULL,
  child_id BIGINT,     -- 외래키
  invitee_id BIGINT,   -- 외래키
  requester_id BIGINT, -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (permission_request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE relationship (
  relationship_id BIGINT AUTO_INCREMENT,
  permission_level ENUM('EDITOR','OWNER','VIEWER') NOT NULL,
  relation_type ENUM('ADMIN','CARER','GUARDIAN','PARENT','TEACHER') NOT NULL,
  child_id BIGINT NOT NULL,  -- 외래키
  member_id BIGINT NOT NULL, -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  PRIMARY KEY (relationship_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE weather (
  weather_id BIGINT AUTO_INCREMENT,
  humidity INT,
  temperature DOUBLE,
  wind_speed DOUBLE,
  weather_desc VARCHAR(255),
  analysis_target_id BIGINT NOT NULL UNIQUE,  -- 외래키
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6),
  recorded_at DATETIME(6),
  PRIMARY KEY (weather_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE analysis_result
  ADD CONSTRAINT FK_analysis_result_analysis_target FOREIGN KEY (analysis_target_id) REFERENCES analysis_target(analysis_target_id);

ALTER TABLE analysis_result_note
  ADD CONSTRAINT FK_analysis_result_note_analysis_result FOREIGN KEY (analysis_result_id) REFERENCES analysis_result(analysis_result_id);

ALTER TABLE analysis_satisfaction
  ADD CONSTRAINT FK_analysis_satisfaction_analysis_result FOREIGN KEY (analysis_result_id) REFERENCES analysis_result(analysis_result_id);

ALTER TABLE analysis_target
  ADD CONSTRAINT FK_analysis_target_child FOREIGN KEY (child_id) REFERENCES child(child_id),
  ADD CONSTRAINT FK_analysis_target_member FOREIGN KEY (uploaded_by) REFERENCES member(member_id);

ALTER TABLE analysis_target_type
  ADD CONSTRAINT FK_analysis_target_type_analysis_target FOREIGN KEY (analysis_target_id) REFERENCES analysis_target(analysis_target_id),
  ADD CONSTRAINT FK_analysis_target_type_analysis_type FOREIGN KEY (analysis_type_id) REFERENCES analysis_type(analysis_type_id);

ALTER TABLE child_background
  ADD CONSTRAINT FK_child_background_child FOREIGN KEY (child_id) REFERENCES child(child_id),
  ADD CONSTRAINT FK_child_background_family_background FOREIGN KEY (family_background_id) REFERENCES family_background(family_background_id);

ALTER TABLE child_diary
  ADD CONSTRAINT FK_child_diary_author FOREIGN KEY (author_id) REFERENCES member(member_id),
  ADD CONSTRAINT FK_child_diary_child FOREIGN KEY (child_id) REFERENCES child(child_id);

ALTER TABLE child_record
  ADD CONSTRAINT FK_child_record_child FOREIGN KEY (child_id) REFERENCES child(child_id);

ALTER TABLE claim
  ADD CONSTRAINT FK_claim_member FOREIGN KEY (member_id) REFERENCES member(member_id);

ALTER TABLE claim_answer
  ADD CONSTRAINT FK_claim_answer_member FOREIGN KEY (member_id) REFERENCES member(member_id),
  ADD CONSTRAINT FK_claim_answer_claim FOREIGN KEY (claim_id) REFERENCES claim(claim_id);

ALTER TABLE guide
  ADD CONSTRAINT FK_guide_member FOREIGN KEY (member_id) REFERENCES member(member_id);

ALTER TABLE health_check
  ADD CONSTRAINT FK_health_check_child FOREIGN KEY (child_id) REFERENCES child(child_id),
  ADD CONSTRAINT FK_health_check_child_record FOREIGN KEY (child_record_id) REFERENCES child_record(child_record_id),
  ADD CONSTRAINT FK_health_check_member FOREIGN KEY (member_id) REFERENCES member(member_id);

ALTER TABLE permission_request
  ADD CONSTRAINT FK_permission_request_child FOREIGN KEY (child_id) REFERENCES child(child_id),
  ADD CONSTRAINT FK_permission_request_invitee FOREIGN KEY (invitee_id) REFERENCES member(member_id),
  ADD CONSTRAINT FK_permission_request_requester FOREIGN KEY (requester_id) REFERENCES member(member_id);

ALTER TABLE relationship
  ADD CONSTRAINT FK_relationship_child FOREIGN KEY (child_id) REFERENCES child(child_id),
  ADD CONSTRAINT FK_relationship_member FOREIGN KEY (member_id) REFERENCES member(member_id);

ALTER TABLE weather
  ADD CONSTRAINT FK_weather_analysis_target FOREIGN KEY (analysis_target_id) REFERENCES analysis_target(analysis_target_id);
  