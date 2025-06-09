package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    // 保存新课程
    Course saveCourse(Course course);

    // 更新课程信息
    Course updateCourse(Course course);

    // 批量删除课程
    void deleteByCourseCodes(List<String> courseCodes);

    // 根据课程编号获取课程信息
    Course getByCourseCode(String courseCode);

    // 获取所有课程信息
    Page<Course> getAllCourses(Pageable pageable);

    // 判断课程是否有老师
    boolean hasTeacher(String courseCode);

    // 根据课程名称获取课程信息
    Page<Course> getByCourseNameContaining(String courseName, Pageable pageable);

    // 获取未分配的课程
    Page<Course> getUnassignedCourses(Pageable pageable);

    // 分页获取老师关联的课程
    Page<Course> getByTeacher(User teacher, Pageable pageable);
}
