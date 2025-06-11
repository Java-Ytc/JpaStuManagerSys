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

    /**
     * 显示登录页面
     * 该方法用于检查会话中是否存在错误信息，如果存在则将其添加到模型中，并从会话中移除该错误信息，
     * 最后返回登录页面的视图。
     *
     * @param request HttpServletRequest 对象，用于获取会话信息
     * @param model   用于传递数据到视图的模型对象
     * @return 登录页面的视图名称
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        Object error = request.getSession().getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
            request.getSession().removeAttribute("error");
        }
        return "/auth/login";
    }

    /**
     * 显示注册页面
     * 该方法直接返回注册页面的视图。
     *
     * @return 注册页面的视图名称
     */
    @GetMapping("/register")
    public String register() {
        return "/auth/register";
    }

    /**
     * 处理用户注册请求
     * 该方法用于处理用户的注册操作，将用户对象保存到数据库中，然后重定向到登录页面。
     *
     * @param user 用户对象，包含用户的注册信息
     * @return 重定向的 URL
     */
    @PostMapping("/register")
    public String processRegister(User user) {
        userService.saveUser(user);
        return "redirect:/auth/login";
    }
}