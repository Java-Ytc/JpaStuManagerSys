package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.CourseSelection;
import com.example.jpastumanagersys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseSelectionRepo extends JpaRepository<CourseSelection, Long> {
    List<CourseSelection> findByStudent(User student);

    Optional<CourseSelection> findByStudentIdAndCourseId(Long studentId, Long courseId);

    Page<CourseSelection> findByStudent(User student, Pageable pageable); // 添加这个方法

    List<CourseSelection> findByCourse(Course course); // 你之前假设需要添加的方法，这里也保留
}