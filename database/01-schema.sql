-- =====================================================================
-- examen-invex: database schema (MySQL 8)
-- Creates the database and the employees table used by the service.
-- Safe to run more than once.
--
--   mysql -u root -p < database/01-schema.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS examen_invex
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE examen_invex;

CREATE TABLE IF NOT EXISTS employees (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    first_name         VARCHAR(50)  NOT NULL,
    middle_name        VARCHAR(50)  NULL,
    paternal_last_name VARCHAR(50)  NOT NULL,
    maternal_last_name VARCHAR(50)  NULL,
    age                INT          NOT NULL,
    gender             VARCHAR(10)  NOT NULL,
    birth_date         DATE         NOT NULL,
    position           VARCHAR(100) NOT NULL,
    -- Stored in UTC, the same as the service (serverTimezone=UTC in the JDBC URL).
    created_at         DATETIME(6)  NOT NULL DEFAULT (UTC_TIMESTAMP(6)),
    active             BIT(1)       NOT NULL DEFAULT b'1',
    CONSTRAINT pk_employees PRIMARY KEY (id),
    CONSTRAINT chk_employees_age CHECK (age BETWEEN 18 AND 100),
    CONSTRAINT chk_employees_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    INDEX idx_employees_first_name (first_name),
    INDEX idx_employees_paternal_last_name (paternal_last_name)
) ENGINE = InnoDB;
