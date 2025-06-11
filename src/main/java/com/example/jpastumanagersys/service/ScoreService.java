package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScoreService {
    Score save(Score score);
    Score updateScore(Score score);

    Score getByStudentAndCourse(Long studentId, Long courseId);

    Page<Score> getScoresByCondition(String courseCode, String courseName, String studentCode, String studentName, Pageable pageable);
}
