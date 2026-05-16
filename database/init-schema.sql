-- HireNest: create database and all tables (run once in MySQL Workbench or CLI)
-- Password in application.properties: Naman@1424

CREATE DATABASE IF NOT EXISTS hirenest
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hirenest;

-- Drop in dependency order (optional — only if you want a clean reset)
-- SET FOREIGN_KEY_CHECKS = 0;
-- DROP TABLE IF EXISTS notifications, messages, saved_jobs, applications, job_skills, jobs, applicant_skills, applicants, recruiters, users;
-- SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    oauth_id VARCHAR(255),
    profile_image_url VARCHAR(512),
    online BIT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS applicants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    phone VARCHAR(100),
    location VARCHAR(255),
    bio TEXT,
    resume_url VARCHAR(512),
    education_json TEXT,
    experience_json TEXT,
    projects_json TEXT,
    profile_completion INT NOT NULL DEFAULT 20,
    CONSTRAINT fk_applicant_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS applicant_skills (
    applicant_id BIGINT NOT NULL,
    skill VARCHAR(255),
    CONSTRAINT fk_applicant_skills FOREIGN KEY (applicant_id) REFERENCES applicants(id)
);

CREATE TABLE IF NOT EXISTS recruiters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    company_name VARCHAR(255) NOT NULL,
    company_logo VARCHAR(512),
    company_description TEXT,
    designation VARCHAR(255),
    CONSTRAINT fk_recruiter_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    salary_min DOUBLE,
    salary_max DOUBLE,
    experience_required VARCHAR(100),
    location VARCHAR(255),
    job_type VARCHAR(50),
    work_mode VARCHAR(50),
    active BIT(1) NOT NULL DEFAULT 1,
    posted_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_job_recruiter FOREIGN KEY (recruiter_id) REFERENCES recruiters(id)
);

CREATE TABLE IF NOT EXISTS job_skills (
    job_id BIGINT NOT NULL,
    skill VARCHAR(255),
    CONSTRAINT fk_job_skills FOREIGN KEY (job_id) REFERENCES jobs(id)
);

CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    applied_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    UNIQUE KEY uk_job_applicant (job_id, applicant_id),
    CONSTRAINT fk_application_job FOREIGN KEY (job_id) REFERENCES jobs(id),
    CONSTRAINT fk_application_applicant FOREIGN KEY (applicant_id) REFERENCES applicants(id)
);

CREATE TABLE IF NOT EXISTS saved_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    saved_at DATETIME(6) NOT NULL,
    UNIQUE KEY uk_saved (applicant_id, job_id),
    CONSTRAINT fk_saved_applicant FOREIGN KEY (applicant_id) REFERENCES applicants(id),
    CONSTRAINT fk_saved_job FOREIGN KEY (job_id) REFERENCES jobs(id)
);

-- Column is is_read (NOT "read") — MySQL reserved word
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    sent_at DATETIME(6) NOT NULL,
    is_read BIT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users(id),
    CONSTRAINT fk_message_receiver FOREIGN KEY (receiver_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    reference_id BIGINT,
    is_read BIT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id)
);
