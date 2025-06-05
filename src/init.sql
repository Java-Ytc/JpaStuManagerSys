-- 检查数据库是否存在，如果不存在则创建数据库，将 your_database_name 替换为实际的数据库名
CREATE DATABASE IF NOT EXISTS your_database_name;

-- 选择要操作的数据库
USE your_database_name;

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
                                     clazz_id BIGINT, -- 新增字段，关联 Clazz 表
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