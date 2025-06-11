package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.service.ClazzService;
import com.example.jpastumanagersys.service.CourseService;
import com.example.jpastumanagersys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/")
    public String home(Model model) {
        long totalUsers = userService.getAllUsers().size();
        long totalTeachers = userService.getAllUsers().stream().filter(user -> "TEACHER".equals(user.getRole())).count();
        long totalClazz = clazzService.getAllClasses().size();
        long totalCourses = courseService.getAllCourses().size();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalTeachers", totalTeachers);
        model.addAttribute("totalClazz", totalClazz);
        model.addAttribute("totalCourses", totalCourses);

        return "home";
    }
}
