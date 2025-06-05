package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    Course saveCourse(Course course);

    Course updateCourse(Course course);

    void deleteCourse(Long id);

    Course getCourseById(Long id);

    List<Course> getAllCourses();

    Page<Course> getAllCourses(Pageable pageable);

    List<Course> getCoursesByTeacher(Long teacherId);
}
