package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, Long> {
    // 根据课程编号查找
    Optional<Course> findByCourseCode(String courseCode);

    // 根据课程编号删除
    void deleteAllByCourseCodeIn(List<String> courseCode);

    // 根据课程名称进行模糊查询
    List<Course> findByCourseNameContaining(String courseName);

    // 查询没有分配老师的课程
    List<Course> findByTeacherIsNull();

    // 根据老师分页查询课程
    List<Course> findByTeacher(User teacher);
}
