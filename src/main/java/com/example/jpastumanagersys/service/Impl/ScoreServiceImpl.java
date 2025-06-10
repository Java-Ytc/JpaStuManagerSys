package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.Score;
import com.example.jpastumanagersys.repo.ScoreRepo;
import com.example.jpastumanagersys.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl implements ScoreService {
    @Autowired
    private ScoreRepo scoreRepo;

    @Override
    public Score save(Score score) {
        return scoreRepo.save(score);
    }

    @Override
    public Score updateScore(Score score) {
        return scoreRepo.save(score);
    }

    @Override
    public Score getByStudentAndCourse(Long studentId, Long courseId) {
        return scoreRepo.findByStudentIdAndCourseId(studentId, courseId);
    }
}
