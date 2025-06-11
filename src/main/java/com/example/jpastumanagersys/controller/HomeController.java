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

    /**
     * 显示主页信息
     * 该方法用于获取系统中的用户总数、教师总数、班级总数和课程总数，并将这些信息添加到模型中，
     * 然后返回主页的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 主页的视图名称
     */
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