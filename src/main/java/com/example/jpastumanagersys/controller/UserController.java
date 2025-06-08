package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // 从 SecurityContext 中获取当前用户的 userCode
        String userCode = getCurrentUserCode();
        User user = userService.getByUserCode(userCode);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Model model) {
        // 从 SecurityContext 中获取当前用户的 userCode
        String userCode = getCurrentUserCode();
        try {
            userService.changePassword(userCode, oldPassword, newPassword);
            model.addAttribute("message", "Password changed successfully");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "profile";
    }

    private String getCurrentUserCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}