package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.config.CustomUserDetails;
import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.CourseSelection;
import com.example.jpastumanagersys.entity.LeaveApplication;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.service.CourseSelectionService;
import com.example.jpastumanagersys.service.CourseService;
import com.example.jpastumanagersys.service.LeaveApplicationService;
import com.example.jpastumanagersys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

import static com.example.jpastumanagersys.util.UserCodeUtils.getCurrentUserCode;

@Controller
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private CourseSelectionService selectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveApplicationService leaveApplicationService;

    @Autowired
    private CourseService courseService;

    /**
     * 学生主页
     * 该方法用于展示学生的主页信息，包含学生的基本信息和选课记录。
     * 首先从安全上下文获取当前登录学生的信息，然后查询该学生的所有选课记录，
     * 最后将学生信息和选课记录添加到模型中，并返回学生主页的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 学生主页的视图名称
     */
    @GetMapping("/dashboard")
    public String studentDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userCode = customUserDetails.getUserCode();
        System.out.println("当前登录的用户编号: " + userCode);
        User student = userService.getByUserCode(userCode);
        Long studentId = student.getId();

        // 查询该学生的所有选课记录
        List<CourseSelection> selections = selectionService.getSelectionsByStudent(studentId);
        model.addAttribute("student", student);
        model.addAttribute("selections", selections);
        return "/student/student-dashboard";
    }

    /**
     * 获取学生的选课记录
     * 该方法用于获取当前登录学生的所有选课记录，并将其添加到模型中，
     * 最后返回展示选课记录的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 展示选课记录的视图名称
     */
    @GetMapping("/selections")
    public String getStudentSelections(Model model) {
        String userCode = getCurrentUserCode();
        Long studentId = userService.getByUserCode(userCode).getId();

        List<CourseSelection> selections = selectionService.getSelectionsByStudent(studentId);
        model.addAttribute("selections", selections);
        return "/student/student-selections";
    }

    /**
     * 显示选课页面
     * 该方法用于展示可供学生选择的课程列表，首先获取所有课程信息，
     * 然后将课程信息添加到模型中，最后返回选课页面的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 选课页面的视图名称
     */
    @GetMapping("/select-course")
    public String showSelectCoursePage(Model model) {
        // 获取所有课程
        Page<Course> courses = courseService.getAllCourses(PageRequest.of(0, 10));
        model.addAttribute("courses", courses.getContent());
        return "/student/student-select-course";
    }

    /**
     * 处理学生选课请求
     * 该方法用于处理学生的选课操作，根据学生 ID 和课程 ID 调用选课服务进行选课。
     * 如果选课成功，将成功消息添加到模型中；如果出现异常，将异常信息添加到模型中，
     * 最后返回选课页面的视图。
     *
     * @param courseId 学生选择的课程 ID
     * @param model    用于传递数据到视图的模型对象
     * @return 选课页面的视图名称
     */
    @PostMapping("/select-course")
    public String selectCourse(@RequestParam Long courseId, Model model) {
        String userCode = getCurrentUserCode();
        Long studentId = userService.getByUserCode(userCode).getId();

        try {
            selectionService.selectCourse(studentId, courseId);
            model.addAttribute("message", "Course selected successfully");
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "/student/student-select-course";
    }

    /**
     * 显示请假申请页面
     * 该方法用于展示学生的请假申请页面，直接返回请假申请页面的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 请假申请页面的视图名称
     */
    @GetMapping("/apply-leave")
    public String showApplyLeavePage(Model model) {
        return "/student/student-apply-leave";
    }

    /**
     * 处理学生请假申请请求
     * 该方法用于处理学生的请假申请操作，根据学生 ID、请假开始日期、结束日期和原因，
     * 调用请假服务进行请假申请。如果申请成功，将成功消息添加到模型中；如果出现异常，
     * 将异常信息添加到模型中，最后返回请假申请页面的视图。
     *
     * @param startDate 请假开始日期
     * @param endDate   请假结束日期
     * @param reason    请假原因
     * @param model     用于传递数据到视图的模型对象
     * @return 请假申请页面的视图名称
     */
    @PostMapping("/apply-leave")
    public String applyLeave(@RequestParam LocalDate startDate,
                             @RequestParam LocalDate endDate,
                             @RequestParam String reason, Model model) {
        String userCode = getCurrentUserCode();
        Long studentId = userService.getByUserCode(userCode).getId();

        try {
            leaveApplicationService.applyForLeave(studentId, startDate, endDate, reason);
            model.addAttribute("message", "Leave application submitted successfully");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "/student/student-apply-leave";
    }

    /**
     * 获取学生的请假记录
     * 该方法用于获取当前登录学生的所有请假记录，并将其添加到模型中，
     * 最后返回展示请假记录的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 展示请假记录的视图名称
     */
    @GetMapping("/leave-applications")
    public String getLeaveApplications(Model model) {
        String userCode = getCurrentUserCode();
        Long studentId = userService.getByUserCode(userCode).getId();

        List<LeaveApplication> leaveApplications = leaveApplicationService.getLeaveApplicationsByStudent(studentId);
        model.addAttribute("leaveApplications", leaveApplications);
        return "/student/student-leave-applications";
    }
}