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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            throw new IllegalStateException("课程已满");
        }

        // 检查学生是否已选该课程
        Optional<CourseSelection> existingSelection = selectionRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (existingSelection.isPresent()) {
            throw new IllegalStateException("该课程已选");
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
        CourseSelection selection = selectionRepository.findByStudentIdAndCourseId(studentId, courseId).orElseThrow(() -> new IllegalArgumentException("无法找到选课记录"));

        // 更新课程选课人数
        Course course = selection.getCourse();
        course.setCurrentStudents(course.getCurrentStudents() - 1);
        courseRepository.save(course);

        selectionRepository.delete(selection);
    }

    @Override
    public List<CourseSelection> getSelectionsByStudent(Long studentId) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("无法通过ID获取学生"));

        return selectionRepository.findByStudent(student);
    }

    @Override
    public Page<CourseSelection> getAllSelections(Pageable pageable) {
        return selectionRepository.findAll(pageable);
    }

    @Override
    public CourseSelection updateScore(Long selectionId, Double score) {
        CourseSelection selection = selectionRepository.findById(selectionId).orElseThrow(() -> new IllegalArgumentException("无法通过ID获取选课信息"));

        selection.setScore(score);
        return selectionRepository.save(selection);
    }

    @Override
    public List<CourseSelection> getSelectionsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("无法通过ID获取课程"));
        return selectionRepository.findByCourse(course);
    }

    @Override
    public CourseSelection getById(Long id) {
        return selectionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Course selection not found"));
    }

    @Override
    public Page<CourseSelection> getSelectionsByCondition(String courseCode, String courseName, String studentCode, String studentName, Pageable pageable) {
        List<CourseSelection> allSelections = selectionRepository.findAll();

        if (courseCode != null && !courseCode.isEmpty()) {
            Course course = courseRepository.findByCourseCode(courseCode).orElseThrow(() -> new IllegalArgumentException("无法找到该课程"));
            allSelections = allSelections.stream()
                    .filter(selection -> selection.getCourse().equals(course))
                    .collect(Collectors.toList());
        }

        if (courseName != null && !courseName.isEmpty()) {
            List<Course> courses = courseRepository.findByCourseNameContaining(courseName);
            allSelections = allSelections.stream()
                    .filter(selection -> courses.contains(selection.getCourse()))
                    .collect(Collectors.toList());
        }

        if (studentCode != null && !studentCode.isEmpty()) {
            User student = userRepository.findByUserCode(studentCode).orElseThrow(() -> new IllegalArgumentException("无法找到该用户"));
            allSelections = allSelections.stream()
                    .filter(selection -> selection.getStudent().equals(student))
                    .collect(Collectors.toList());
        }

        if (studentName != null && !studentName.isEmpty()) {
            List<User> students = userRepository.findByUsernameContaining(studentName);
            allSelections = allSelections.stream()
                    .filter(selection -> students.contains(selection.getStudent()))
                    .collect(Collectors.toList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allSelections.size());

        return new PageImpl<>(allSelections.subList(start, end), pageable, allSelections.size());
    }
}