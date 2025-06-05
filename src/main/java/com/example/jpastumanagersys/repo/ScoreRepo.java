package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepo extends JpaRepository<Score, Long> {
    List<Score> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
