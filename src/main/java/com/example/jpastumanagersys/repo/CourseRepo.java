package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, Long> {
    // 根据课程编号查找
    Optional<Course> findByCourseCode(String courseCode);

    // 根据课程编号删除
    void deleteAllByCourseCodeIn(List<String> courseCode);

    Page<Course> findByCourseNameContaining(String courseName, Pageable pageable);

    List<Course> findByTeacherId(Long teacherId);
}
