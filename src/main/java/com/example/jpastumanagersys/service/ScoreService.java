package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.Score;

import java.util.List;

public interface ScoreService {
    Score saveScore(Score score);
    Score updateScore(Score score);
    void deleteScore(Long id);
    Score getScoreById(Long id);
    List<Score> getScoresByStudentAndCourse(Long studentId, Long courseId);
}
