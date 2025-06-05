-- 创建数据库，如果不存在则创建
CREATE DATABASE IF NOT EXISTS jpa_stu_manager_sys;

-- 使用创建好的数据库
USE jpa_stu_manager_sys;

-- 创建 users 表
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(255) NOT NULL,
                                     email VARCHAR(255),
                                     phone VARCHAR(255)
);

-- 创建 courses 表
CREATE TABLE IF NOT EXISTS courses (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       courseName VARCHAR(255) NOT NULL,
                                       courseCode VARCHAR(255) NOT NULL,
                                       maxStudents INT NOT NULL,
                                       currentStudents INT NOT NULL,
                                       courseTime VARCHAR(255),
                                       teacher_id BIGINT,
                                       FOREIGN KEY (teacher_id) REFERENCES users(id)
);

-- 创建 course_selection 表
CREATE TABLE IF NOT EXISTS course_selection (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                student_id BIGINT NOT NULL,
                                                course_id BIGINT NOT NULL,
                                                selectionDate DATETIME,
                                                score DOUBLE,
                                                FOREIGN KEY (student_id) REFERENCES users(id),
                                                FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- 创建 attendance 表
CREATE TABLE IF NOT EXISTS attendance (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          student_id BIGINT NOT NULL,
                                          course_id BIGINT NOT NULL,
                                          attendanceDate DATETIME,
                                          isPresent BOOLEAN,
                                          FOREIGN KEY (student_id) REFERENCES users(id),
                                          FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- 创建 scores 表
CREATE TABLE IF NOT EXISTS scores (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      student_id BIGINT NOT NULL,
                                      course_id BIGINT NOT NULL,
                                      score DOUBLE,
                                      FOREIGN KEY (student_id) REFERENCES users(id),
                                      FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- 创建 Clazz 表
CREATE TABLE IF NOT EXISTS Clazz (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     className VARCHAR(255),
                                     classCode VARCHAR(255)
);

-- 创建 leave_applications 表
CREATE TABLE IF NOT EXISTS leave_applications (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  student_id BIGINT NOT NULL,
                                                  startDate DATETIME,
                                                  endDate DATETIME,
                                                  reason VARCHAR(255),
                                                  approved BOOLEAN,
                                                  FOREIGN KEY (student_id) REFERENCES users(id)
);

-- 插入 users 表的初始化数据
INSERT INTO users (username, password, role, email, phone) VALUES
                                                               ('admin', 'admin123', 'ADMIN', 'admin@example.com', '1234567890'),
                                                               ('teacher1', 'teacher123', 'TEACHER', 'teacher1@example.com', '0987654321'),
                                                               ('student1', 'student123', 'STUDENT', 'student1@example.com', '1122334455');

-- 插入 courses 表的初始化数据
INSERT INTO courses (courseName, courseCode, maxStudents, currentStudents, courseTime, teacher_id) VALUES
                                                                                                       ('Mathematics', 'MATH101', 30, 0, 'Monday 9:00 AM', 2),
                                                                                                       ('Physics', 'PHYS101', 25, 0, 'Wednesday 10:30 AM', 2);

-- 插入 course_selection 表的初始化数据
INSERT INTO course_selection (student_id, course_id, selectionDate, score) VALUES
    (3, 1, '2024-01-01 10:00:00', NULL);

-- 插入 attendance 表的初始化数据
INSERT INTO attendance (student_id, course_id, attendanceDate, isPresent) VALUES
    (3, 1, '2024-01-02 09:00:00', true);

-- 插入 scores 表的初始化数据
INSERT INTO scores (student_id, course_id, score) VALUES
    (3, 1, 85.0);

-- 插入 Clazz 表的初始化数据
INSERT INTO Clazz (className, classCode) VALUES
    ('Class A', 'CLA01');

-- 插入 leave_applications 表的初始化数据
INSERT INTO leave_applications (student_id, startDate, endDate, reason, approved) VALUES
    (3, '2024-01-10 00:00:00', '2024-01-12 00:00:00', 'Sick leave', false);