package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.CourseSelection;
import com.example.jpastumanagersys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseSelectionRepo extends JpaRepository<CourseSelection, Long> {
    List<CourseSelection> findByStudent(User student);

    Optional<CourseSelection> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<CourseSelection> findByCourse(Course course);
}