package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public String listCourses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Page<Course> coursePage = courseService.getAllCourses(PageRequest.of(page, size));
        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        return "course-list";
    }

    @GetMapping("/add")
    public String addCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "course-form";
    }

    @PostMapping("/save")
    public String saveCourse(@ModelAttribute Course course) {
        courseService.saveCourse(course);
        return "redirect:/course/list";
    }

    @GetMapping("/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "course-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/course/list";
    }
}
