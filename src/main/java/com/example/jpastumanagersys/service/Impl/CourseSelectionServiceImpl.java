package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.CourseSelection;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.repo.CourseRepo;
import com.example.jpastumanagersys.repo.CourseSelectionRepo;
import com.example.jpastumanagersys.repo.UserRepo;
import com.example.jpastumanagersys.service.CourseSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseSelectionServiceImpl implements CourseSelectionService {
    @Autowired
    private CourseSelectionRepo selectionRepository;

    @Autowired
    private CourseRepo courseRepository;

    @Autowired
    private UserRepo userRepository;

    @Override
    public CourseSelection selectCourse(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // 检查课程是否已满
        if (course.getCurrentStudents() >= course.getMaxStudents()) {
            throw new IllegalStateException("Course is full");
        }

        // 检查学生是否已选该课程
        Optional<CourseSelection> existingSelection = selectionRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (existingSelection.isPresent()) {
            throw new IllegalStateException("Student already selected this course");
        }

        CourseSelection selection = new CourseSelection();
        selection.setStudent(student);
        selection.setCourse(course);
        selection.setSelectionDate(new Date());

        // 更新课程选课人数
        course.setCurrentStudents(course.getCurrentStudents() + 1);
        courseRepository.save(course);

        return selectionRepository.save(selection);
    }

    @Override
    public void dropCourse(Long studentId, Long courseId) {
        CourseSelection selection = selectionRepository.findByStudentIdAndCourseId(studentId, courseId).orElseThrow(() -> new IllegalArgumentException("Course selection not found"));

        // 更新课程选课人数
        Course course = selection.getCourse();
        course.setCurrentStudents(course.getCurrentStudents() - 1);
        courseRepository.save(course);

        selectionRepository.delete(selection);
    }

    @Override
    public List<CourseSelection> getSelectionsByStudent(Long studentId) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        return selectionRepository.findByStudent(student);
    }

    @Override
    public Page<CourseSelection> getSelectionsByStudent(Long studentId, Pageable pageable) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        return selectionRepository.findByStudent(student, pageable);
    }

    @Override
    public Page<CourseSelection> getAllSelections(Pageable pageable) {
        return selectionRepository.findAll(pageable);
    }

    @Override
    public CourseSelection updateScore(Long selectionId, Double score) {
        CourseSelection selection = selectionRepository.findById(selectionId).orElseThrow(() -> new IllegalArgumentException("Course selection not found"));

        selection.setScore(score);
        return selectionRepository.save(selection);
    }

    @Override
    public List<CourseSelection> getSelectionsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return selectionRepository.findByCourse(course);
    }
}