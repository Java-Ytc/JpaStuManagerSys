package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.CourseSelection;
import com.example.jpastumanagersys.service.CourseSelectionService;
import com.example.jpastumanagersys.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSelectionService selectionService;

    @GetMapping("/dashboard")
    public String teacherDashboard(Model model) {
        // 实际应用中，应从SecurityContext获取当前用户ID
        Long teacherId = 2L;
        List<Course> courses = courseService.getCoursesByTeacher(teacherId);
        model.addAttribute("courses", courses);
        return "/teacher/teacher-dashboard";
    }

    @GetMapping("/course/{courseId}/students")
    public String viewCourseStudents(@PathVariable Long courseId, Model model) {
        List<CourseSelection> selections = selectionService.getSelectionsByCourse(courseId);
        model.addAttribute("selections", selections);
        return "teacher/teacher-course-students";
    }
}
