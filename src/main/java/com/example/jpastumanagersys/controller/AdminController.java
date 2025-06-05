package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        long totalStudents = userService.getAllUsers().stream().filter(user -> "STUDENT".equals(user.getRole())).count();

        long totalTeachers = userService.getAllUsers().stream().filter(user -> "TEACHER".equals(user.getRole())).count();

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalTeachers", totalTeachers);

        return "admin-dashboard";
    }
}
