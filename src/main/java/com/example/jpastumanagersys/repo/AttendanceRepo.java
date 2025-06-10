package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.Attendance;
import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(User student);

    List<Attendance> findByCourse(Course course);

    Page<Attendance> findByCourse(Course course, Pageable pageable); // 添加这个方法

    // 根据学生和课程以及出勤状态查询缺课记录
    List<Attendance> findByStudentAndCourseAndIsPresentFalse(User student, Course course);
}