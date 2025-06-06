package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.CourseSelection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseSelectionService {
    CourseSelection selectCourse(Long studentId, Long courseId);

    void dropCourse(Long studentId, Long courseId);

    List<CourseSelection> getSelectionsByStudent(Long studentId);

    Page<CourseSelection> getSelectionsByStudent(Long studentId, Pageable pageable);

    Page<CourseSelection> getAllSelections(Pageable pageable);

    CourseSelection updateScore(Long selectionId, Double score);

    // 添加新方法
    List<CourseSelection> getSelectionsByCourse(Long courseId);
}