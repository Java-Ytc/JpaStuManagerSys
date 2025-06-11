package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.Score;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.repo.CourseRepo;
import com.example.jpastumanagersys.repo.ScoreRepo;
import com.example.jpastumanagersys.repo.UserRepo;
import com.example.jpastumanagersys.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements ScoreService {
    @Autowired
    private ScoreRepo scoreRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private UserRepo userRepo;


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

    @Override
    public Page<Score> getScoresByCondition(String courseCode, String courseName, String studentCode, String studentName, Pageable pageable) {
        List<Score> allScores = scoreRepo.findAll();

        if (courseCode != null && !courseCode.isEmpty()) {
            Course course = courseRepo.findByCourseCode(courseCode).orElseThrow(() -> new IllegalArgumentException("没有找到课程"));
            allScores = allScores.stream()
                    .filter(score -> score.getCourse().equals(course))
                    .collect(Collectors.toList());
        }

        if (courseName != null && !courseName.isEmpty()) {
            List<Course> courses = courseRepo.findByCourseNameContaining(courseName);
            allScores = allScores.stream()
                    .filter(score -> courses.contains(score.getCourse()))
                    .collect(Collectors.toList());
        }

        if (studentCode != null && !studentCode.isEmpty()) {
            User student = userRepo.findByUserCode(studentCode).orElseThrow(() -> new IllegalArgumentException("找不到该用户"));
            allScores = allScores.stream()
                    .filter(score -> score.getStudent().equals(student))
                    .collect(Collectors.toList());
        }

        if (studentName != null && !studentName.isEmpty()) {
            List<User> students = userRepo.findByUsernameContaining(studentName);
            allScores = allScores.stream()
                    .filter(score -> students.contains(score.getStudent()))
                    .collect(Collectors.toList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allScores.size());

        return new PageImpl<>(allScores.subList(start, end), pageable, allScores.size());
    }
}
