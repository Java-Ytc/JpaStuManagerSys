package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepo extends JpaRepository<Score, Long> {
    Score findByStudentIdAndCourseId(Long studentId, Long courseId);
}
