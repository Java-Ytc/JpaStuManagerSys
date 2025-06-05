package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.CourseSelection;
import com.example.jpastumanagersys.service.CourseSelectionService;
import com.example.jpastumanagersys.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/selection")
public class CourseSelectionController {
    @Autowired
    private CourseSelectionService selectionService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public String listSelections(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        // 实际应用中，应从SecurityContext获取当前用户ID
        Long studentId = 1L;
        Page<CourseSelection> selectionPage = selectionService.getSelectionsByStudent(studentId, PageRequest.of(page, size));
        model.addAttribute("selections", selectionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", selectionPage.getTotalPages());
        return "selection-list";
    }

    @GetMapping("/add/{courseId}")
    public String selectCourse(@PathVariable Long courseId) {
        // 实际应用中，应从SecurityContext获取当前用户ID
        Long studentId = 1L;
        selectionService.selectCourse(studentId, courseId);
        return "redirect:/course/list";
    }

    @GetMapping("/delete/{courseId}")
    public String dropCourse(@PathVariable Long courseId) {
        // 实际应用中，应从SecurityContext获取当前用户ID
        Long studentId = 1L;
        selectionService.dropCourse(studentId, courseId);
        return "redirect:/selection/list";
    }
}
