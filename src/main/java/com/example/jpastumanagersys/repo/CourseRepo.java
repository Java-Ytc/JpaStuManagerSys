package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepo extends JpaRepository<Course, Long> {
    List<Course> findByTeacherId(Long teacherId);
}
