package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        return "/admin/admin-dashboard";
    }

    // 显示学生列表页面
    @GetMapping("/students")
    public String listStudents(@RequestParam(required = false) String userCode,
                               @RequestParam(required = false) String username,
                               @RequestParam(required = false) Long classId,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        Page<User> studentPage = userService.getStudentsByCondition(userCode, username, classId, PageRequest.of(page, size));
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        // 检查是否有学生数据
        if (studentPage.isEmpty()) {
            model.addAttribute("noStudentsMessage", "No students found.");
        }
        return "/admin/admin-student-list";
    }

    // 批量删除学生信息
    @PostMapping("/students/delete")
    public String deleteStudents(@RequestParam List<String> userCodes) {
        userService.deleteStudentsByUserCodes(userCodes);
        return "redirect:/admin/students";
    }

    // 显示添加学生页面
    @GetMapping("/students/add")
    public String addStudentForm(Model model) {
        model.addAttribute("user", new User());
        return "/admin/admin-student-add-form";
    }

    // 保存新添加的学生信息
    @PostMapping("/students/save")
    public String saveStudent(@ModelAttribute User user) {
        user.setRole("STUDENT");
        userService.saveUser(user);
        return "redirect:/admin/students";
    }

    // 显示编辑学生页面
    @GetMapping("/students/edit/{userCode}")
    public String editStudentForm(@PathVariable String userCode, Model model) {
        User user = userService.getUserByUserCode(userCode);
        model.addAttribute("user", user);
        return "/admin/admin-student-edit-form";
    }

    // 更新学生信息
    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute User user) {
        System.out.println("接受到的userCode：" + user.getUserCode());
        userService.updateUser(user);
        return "redirect:/admin/students";
    }
}
