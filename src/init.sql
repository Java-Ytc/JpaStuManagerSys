-- 检查数据库是否存在，如果不存在则创建数据库，将 your_database_name 替换为实际的数据库名
CREATE DATABASE IF NOT EXISTS jpa_stu_manager_sys;

-- 选择要操作的数据库，将 your_database_name 替换为实际的数据库名
USE jpa_stu_manager_sys;

-- 删除表时按照逆依赖顺序，避免外键约束错误
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS course_selection;
DROP TABLE IF EXISTS scores;
DROP TABLE IF EXISTS leave_applications;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS Clazz;

-- 创建 Clazz 表
CREATE TABLE IF NOT EXISTS Clazz (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     className VARCHAR(255),
                                     classCode VARCHAR(255)
);

-- 创建 users 表
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(255) NOT NULL,
                                     email VARCHAR(255),
                                     phone VARCHAR(255),
                                     clazz_id BIGINT,
                                     FOREIGN KEY (clazz_id) REFERENCES Clazz(id)
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
                                      student_id BIGINT,
                                      course_id BIGINT,
                                      score DOUBLE,
                                      FOREIGN KEY (student_id) REFERENCES users(id),
                                      FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- 创建 leave_applications 表
CREATE TABLE IF NOT EXISTS leave_applications (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  student_id BIGINT,
                                                  startDate DATE,
                                                  endDate DATE,
                                                  reason TEXT,
                                                  approved BOOLEAN,
                                                  FOREIGN KEY (student_id) REFERENCES users(id)
);

-- 插入 Clazz 表数据
INSERT INTO Clazz (className, classCode) VALUES
                                             ('Class A', 'CL001'),
                                             ('Class B', 'CL002');

-- 插入 users 表数据
INSERT INTO users (username, password, role, email, phone, clazz_id) VALUES
                                                                         ('teacher1', 'password1', 'TEACHER', 'teacher1@example.com', '1234567890', NULL),
                                                                         ('student1', 'password2', 'STUDENT', 'student1@example.com', '0987654321', (SELECT id FROM Clazz WHERE classCode = 'CL001')),
                                                                         ('student2', 'password3', 'STUDENT', 'student2@example.com', '1122334455', (SELECT id FROM Clazz WHERE classCode = 'CL002'));

-- 插入 courses 表数据
INSERT INTO courses (courseName, courseCode, maxStudents, currentStudents, courseTime, teacher_id) VALUES
                                                                                                       ('Mathematics', 'MATH101', 30, 0, 'Mon/Wed/Fri 9:00-10:30', (SELECT id FROM users WHERE username = 'teacher1')),
                                                                                                       ('Physics', 'PHYS101', 25, 0, 'Tue/Thu 13:00-14:30', (SELECT id FROM users WHERE username = 'teacher1'));

-- 插入 course_selection 表数据
INSERT INTO course_selection (student_id, course_id, selectionDate, score) VALUES
                                                                               ((SELECT id FROM users WHERE username = 'student1'), (SELECT id FROM courses WHERE courseCode = 'MATH101'), '2024-01-01 09:00:00', NULL),
                                                                               ((SELECT id FROM users WHERE username = 'student2'), (SELECT id FROM courses WHERE courseCode = 'PHYS101'), '2024-01-02 10:00:00', NULL);

-- 更新课程当前选课人数
UPDATE courses
SET currentStudents = (SELECT COUNT(*) FROM course_selection WHERE course_id = courses.id)
WHERE id IN (SELECT course_id FROM course_selection);

-- 插入 attendance 表数据
INSERT INTO attendance (student_id, course_id, attendanceDate, isPresent) VALUES
                                                                              ((SELECT id FROM users WHERE username = 'student1'), (SELECT id FROM courses WHERE courseCode = 'MATH101'), '2024-01-02 09:00:00', TRUE),
                                                                              ((SELECT id FROM users WHERE username = 'student2'), (SELECT id FROM courses WHERE courseCode = 'PHYS101'), '2024-01-03 13:00:00', FALSE);

-- 插入 scores 表数据
INSERT INTO scores (student_id, course_id, score) VALUES
                                                      ((SELECT id FROM users WHERE username = 'student1'), (SELECT id FROM courses WHERE courseCode = 'MATH101'), 85),
                                                      ((SELECT id FROM users WHERE username = 'student2'), (SELECT id FROM courses WHERE courseCode = 'PHYS101'), 90);

-- 插入 leave_applications 表数据
INSERT INTO leave_applications (student_id, startDate, endDate, reason, approved) VALUES
    ((SELECT id FROM users WHERE username = 'student1'), '2024-01-10', '2024-01-12', 'Sick leave', FALSE);