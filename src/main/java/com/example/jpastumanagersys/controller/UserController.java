package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.config.CustomUserDetails;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/update-profile-form")
    public String showUpdateProfilePage(Model model) {
        // 获取当前登录用户的信息
        String userCode = getCurrentUserCode();
        User student = userService.getByUserCode(userCode);
        model.addAttribute("student", student);
        return "/user/update-profile-form";
    }

    @GetMapping("/change-password-form")
    public String showChangePasswordPage() {
        return "/user/change-password-form";
    }


    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user, Model model) {
        // 从 SecurityContext 中获取当前用户的 userCode
        String userCode = getCurrentUserCode();
        user.setUserCode(userCode);
        try {
            userService.updateStudent(user);
            System.out.println("修改用户信息成功");
            // 修改成功，重定向到学生主页
            return "redirect:/student/dashboard";
        } catch (IllegalArgumentException e) {
            System.out.println("修改用户信息失败");
            model.addAttribute("error", e.getMessage());
            // 修改失败，重新获取学生信息并添加到模型中
            User student = userService.getByUserCode(userCode);
            model.addAttribute("student", student);
            // 返回修改信息表单页面
            return "/user/update-profile-form";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Model model) {
        // 从 SecurityContext 中获取当前用户的 userCode
        String userCode = getCurrentUserCode();
        try {
            userService.changePassword(userCode, oldPassword, newPassword);
            // 修改成功，重定向到学生主页
            return "redirect:/student/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            // 修改失败，返回修改密码表单页面
            return "/user/change-password-form";
        }
    }

    private String getCurrentUserCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return customUserDetails.getUserCode();
    }
}