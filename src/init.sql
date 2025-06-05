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