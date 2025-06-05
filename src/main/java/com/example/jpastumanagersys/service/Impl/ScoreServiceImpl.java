package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.Score;
import com.example.jpastumanagersys.repo.ScoreRepo;
import com.example.jpastumanagersys.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {
    @Autowired
    private ScoreRepo scoreRepository;

    @Override
    public Score saveScore(Score score) {
        return scoreRepository.save(score);
    }

    @Override
    public Score updateScore(Score score) {
        return scoreRepository.save(score);
    }

    @Override
    public void deleteScore(Long id) {
        scoreRepository.deleteById(id);
    }

    @Override
    public Score getScoreById(Long id) {
        return scoreRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Score not found"));
    }

    @Override
    public List<Score> getScoresByStudentAndCourse(Long studentId, Long courseId) {
        return scoreRepository.findByStudentIdAndCourseId(studentId, courseId);
    }
}
