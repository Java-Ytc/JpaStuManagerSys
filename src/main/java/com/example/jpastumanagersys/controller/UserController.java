package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        // 实际应用中，应从SecurityContext获取当前用户
        User user = userService.getUserById(1L);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Model model) {
        // 实际应用中，应从SecurityContext获取当前用户ID
        try {
            userService.changePassword(1L, oldPassword, newPassword);
            model.addAttribute("message", "Password changed successfully");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "profile";
    }
}
