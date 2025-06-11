package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        Object error = request.getSession().getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
            request.getSession().removeAttribute("error");
        }
        return "/auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "/auth/register";
    }

    @PostMapping("/register")
    public String processRegister(User user) {
        userService.saveUser(user);
        return "redirect:/auth/login";
    }
}
