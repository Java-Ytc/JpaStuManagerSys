package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.Score;

public interface ScoreService {
    Score save(Score score);
    Score updateScore(Score score);

    Score getByStudentAndCourse(Long studentId, Long courseId);
}
