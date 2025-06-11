package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.example.jpastumanagersys.util.UserCodeUtils.getCurrentUserCode;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 显示用户信息修改页面
     * 该方法用于获取当前登录用户的信息，并将其添加到模型中，然后返回用户信息修改页面的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 用户信息修改页面的视图名称
     */
    @GetMapping("/update-profile-form")
    public String showUpdateProfilePage(Model model) {
        // 获取当前登录用户的信息
        String userCode = getCurrentUserCode();
        User student = userService.getByUserCode(userCode);
        model.addAttribute("student", student);
        return "/user/update-profile-form";
    }

    /**
     * 显示密码修改页面
     * 该方法直接返回密码修改页面的视图。
     *
     * @return 密码修改页面的视图名称
     */
    @GetMapping("/change-password-form")
    public String showChangePasswordPage() {
        return "/user/change-password-form";
    }

    /**
     * 更新用户信息
     * 该方法用于处理用户信息的更新操作，首先从安全上下文中获取当前用户的 userCode，
     * 然后将其设置到用户对象中，调用用户服务进行更新。如果更新成功，重定向到学生主页；
     * 如果出现异常，将异常信息添加到模型中，并返回用户信息修改页面的视图。
     *
     * @param user  用户对象，包含要更新的用户信息
     * @param model 用于传递数据到视图的模型对象
     * @return 重定向的 URL 或视图名称
     */
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

    /**
     * 修改用户密码
     * 该方法用于处理用户密码的修改操作，首先从安全上下文中获取当前用户的 userCode，
     * 然后调用用户服务进行密码修改。如果修改成功，重定向到学生主页；
     * 如果出现异常，将异常信息添加到模型中，并返回密码修改页面的视图。
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param model       用于传递数据到视图的模型对象
     * @return 重定向的 URL 或视图名称
     */
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
}