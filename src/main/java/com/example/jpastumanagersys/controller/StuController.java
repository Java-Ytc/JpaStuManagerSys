package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.CourseSelection;
import com.example.jpastumanagersys.service.CourseSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/stu")
public class StuController {
    @Autowired
    private CourseSelectionService selectionService;

    @GetMapping("/dashboard")
    public String studentDashboard(Model model) {
        // 实际应用中，应从SecurityContext获取当前用户ID
        Long studentId = 3L;
        List<CourseSelection> selections = selectionService.getSelectionsByStudent(studentId);
        model.addAttribute("selections", selections);
        return "student-dashboard";
    }
}
