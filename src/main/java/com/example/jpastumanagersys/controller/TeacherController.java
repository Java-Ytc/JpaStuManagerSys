package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.config.CustomUserDetails;
import com.example.jpastumanagersys.entity.*;
import com.example.jpastumanagersys.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSelectionService selectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 老师主页
     * 该方法用于展示老师的主页信息，包含老师的基本信息和所教授的课程信息。
     * 首先从安全上下文获取当前登录老师的信息，然后查询该老师所教授的所有课程，
     * 最后将老师信息和课程信息添加到模型中，并返回老师主页的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 老师主页的视图名称
     */
    @GetMapping("/dashboard")
    public String teacherDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userCode = customUserDetails.getUserCode();
        User teacher = userService.getByUserCode(userCode);

        Page<Course> courses = courseService.getByTeacher(teacher, PageRequest.of(0, 10));

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses.getContent());
        return "/teacher/teacher-dashboard";
    }

    /**
     * 显示课程详细信息页面
     * 该方法用于展示选择某个课程的所有学生的信息，并允许老师为学生打分。
     * 首先根据课程 ID 查询选课学生的信息，然后将课程信息和学生信息添加到模型中，
     * 最后返回课程详细信息页面的视图。
     *
     * @param courseCode 课程 编号
     * @param model      用于传递数据到视图的模型对象
     * @return 课程详细信息页面的视图名称
     */
    @GetMapping("/course/detail/{courseCode}/{courseId}")
    public String showCourseDetail(@PathVariable String courseCode, @PathVariable Long courseId, Model model) {
        Course course = courseService.getByCourseCode(courseCode);
        List<CourseSelection> selections = selectionService.getSelectionsByCourse(courseId);

        for (CourseSelection selection : selections) {
            User student = selection.getStudent();
            Score score = scoreService.getByStudentAndCourse(student.getId(), course.getId());
            if (score != null) {
                selection.setScore(score.getScore());
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("selections", selections);
        return "/teacher/teacher-course-detail";
    }

    /**
     * 处理为学生打分的请求
     * 该方法用于处理老师为学生打分的操作，根据选课记录 ID 和分数，
     * 调用分数服务进行分数保存或更新。如果操作成功，将成功消息添加到模型中；
     * 如果出现异常，将异常信息添加到模型中，最后返回课程详细信息页面的视图。
     *
     * @param selectionId 选课记录 ID
     * @param score       分数
     * @param model       用于传递数据到视图的模型对象
     * @return 课程详细信息页面的视图名称
     */
    @PostMapping("/course/detail/{courseId}/score")
    public String saveScore(@PathVariable Long courseId, @RequestParam Long selectionId, @RequestParam Double score, Model model) {
        CourseSelection selection = selectionService.getById(selectionId);
        User student = selection.getStudent();
        Course course = selection.getCourse();

        // 创建新的分数记录
        Score scoreObj = new Score();
        scoreObj.setStudent(student);
        scoreObj.setCourse(course);
        scoreObj.setScore(score);
        scoreService.save(scoreObj);

        // 更新选课记录中的分数
        selection.setScore(score);
        selectionService.updateScore(selectionId, score);

        // 获取课程编号
        Course currentCourse = courseService.getById(courseId);
        String courseCode = currentCourse.getCourseCode();

        // 重定向到包含课程编号和课程 ID 的 URL
        return "redirect:/teacher/course/detail/" + courseCode + "/" + courseId;
    }

    @GetMapping("/course/detail/{courseId}/absence/{selectionId}")
    public String showAbsencePage(@PathVariable Long courseId, @PathVariable Long selectionId, Model model) {
        CourseSelection selection = selectionService.getById(selectionId);
        Course course = courseService.getById(courseId); // 获取课程信息
        User student = selection.getStudent();

        // 获取该学生该课程的所有缺课记录
        List<Attendance> absences = attendanceService.getAttendanceByStudentAndCourse(student.getId(), course.getId());

        model.addAttribute("selection", selection);
        model.addAttribute("course", course); // 添加课程信息到模型
        model.addAttribute("absences", absences); // 添加缺课记录到模型

        return "/teacher/teacher-absence-form";
    }

    @PostMapping("/course/detail/{courseId}/absence/{selectionId}")
    public String saveAbsence(@PathVariable Long courseId, @PathVariable Long selectionId, Model model) {
        CourseSelection selection = selectionService.getById(selectionId);
        User student = selection.getStudent();
        Course course = selection.getCourse();

        // 记录缺课信息
        attendanceService.recordAttendance(student.getId(), course.getId(), false);

        // 获取课程编号
        Course currentCourse = courseService.getById(courseId);
        String courseCode = currentCourse.getCourseCode();

        // 重定向到包含课程编号和课程 ID 的 URL
        return "redirect:/teacher/course/detail/" + courseCode + "/" + courseId;
    }

    /**
     * 显示某个课程下某个学生的所有缺课记录
     *
     * @param courseId    课程 ID
     * @param selectionId 选课记录 ID
     * @param model       用于传递数据到视图的模型对象
     * @return 显示缺课记录的页面视图名称
     */
    @GetMapping("/course/detail/{courseId}/absence/records/{selectionId}")
    public String showAbsenceRecords(@PathVariable Long courseId, @PathVariable Long selectionId, Model model) {
        CourseSelection selection = selectionService.getById(selectionId);
        Course course = courseService.getById(courseId);
        User student = selection.getStudent();

        // 获取该学生该课程的所有缺课记录
        List<Attendance> absences = attendanceService.getAttendanceByStudentAndCourse(student.getId(), course.getId());

        model.addAttribute("selection", selection);
        model.addAttribute("course", course);
        model.addAttribute("student", student);
        model.addAttribute("absences", absences);

        return "/teacher/teacher-absence-records"; // 返回显示缺课记录的页面
    }
}
