package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.Clazz;
import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.service.ClazzService;
import com.example.jpastumanagersys.service.CourseService;
import com.example.jpastumanagersys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private CourseService courseService;

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
                               @RequestParam(required = false) String classCode,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {

        System.out.println("学生编号："+userCode);
        System.out.println("用户名："+username);
        System.out.println("接收到的班级编号：" + classCode);

        Page<User> studentPage;
        if (userCode != null && !userCode.isEmpty()) {
            // 若学生编号存在，则仅按学生编号查询
            studentPage = userService.getStudentsByCondition(userCode, null, null, PageRequest.of(page, size));
        } else {
            // 否则，根据用户名、班级编号或联合查询
            studentPage = userService.getStudentsByCondition(null, username, classCode, PageRequest.of(page, size));
        }

        System.out.println("查询到的学生数量："+studentPage.getContent().size());

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
        userService.deleteByUserCodes(userCodes);
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
        User user = userService.getByUserCode(userCode);
        model.addAttribute("user", user);
        return "/admin/admin-student-edit-form";
    }

    // 更新学生信息
    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute User user) {
        System.out.println("接受到的userCode：" + user.getUserCode());
        userService.updateStudent(user);
        return "redirect:/admin/students";
    }

    @GetMapping("/teachers")
    public String listTeachers(@RequestParam(required = false) String userCode,
                               @RequestParam(required = false) String username,
                               @RequestParam(required = false) String courseCode,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        Page<User> teacherPage;
        if (userCode != null && !userCode.isEmpty()) {
            // 若教师编号存在，则仅按教师编号查询
            teacherPage = userService.getTeachersByCondition(userCode, null, null, PageRequest.of(page, size));
        } else {
            // 否则，根据用户名、课程编号或联合查询
            teacherPage = userService.getTeachersByCondition(null, username, courseCode, PageRequest.of(page, size));
        }
        model.addAttribute("teachers", teacherPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", teacherPage.getTotalPages());
        // 检查是否有教师数据
        if (teacherPage.isEmpty()) {
            model.addAttribute("noTeachersMessage", "No teachers found.");
        }
        return "/admin/admin-teacher-list";
    }

    // 显示添加教师页面
    @GetMapping("/teachers/add")
    public String addTeacherForm(Model model) {
        model.addAttribute("user", new User());
        return "/admin/admin-teacher-add-form";
    }

    // 保存新添加的教师信息
    @PostMapping("/teachers/save")
    public String saveTeacher(@ModelAttribute User user) {
        user.setRole("TEACHER");
        userService.saveUser(user);
        return "redirect:/admin/teachers";
    }

    // 显示编辑教师页面
    @GetMapping("/teachers/edit/{userCode}")
    public String editTeacherForm(@PathVariable String userCode, Model model) {
        User user = userService.getByUserCode(userCode);
        model.addAttribute("user", user);
        return "/admin/admin-teacher-edit-form";
    }

    // 更新教师信息
    @PostMapping("/teachers/update")
    public String updateTeacher(@ModelAttribute User user) {
        userService.updateTeacher(user);
        return "redirect:/admin/teachers";
    }

    // 批量删除教师信息
    @PostMapping("/teachers/delete")
    public String deleteTeachers(@RequestParam List<String> userCodes) {
        userService.deleteByUserCodes(userCodes); // 这里复用删除学生的方法，因为逻辑相同
        return "redirect:/admin/teachers";
    }

    // 显示班级列表页面，支持查询功能
    @GetMapping("/classes")
    public String listClasses(@RequestParam(required = false) String classCode,
                              @RequestParam(required = false) String className,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {

        Page<Clazz> clazzPage;
        if (classCode != null && !classCode.isEmpty()) {
            // 若班级编号存在，则仅按班级编号查询
            Clazz clazz = clazzService.getByClassCode(classCode);
            if (clazz != null) {
                clazzPage = new PageImpl<>(Collections.singletonList(clazz), PageRequest.of(page, size), 1);
            } else {
                clazzPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
            }
        } else if (className != null && !className.isEmpty()) {
            // 按班级名称查询
            clazzPage = clazzService.getByClassNameContaining(className, PageRequest.of(page, size));
        } else {
            // 查询所有班级
            clazzPage = clazzService.getAllClasses(PageRequest.of(page, size));
        }

        model.addAttribute("classes", clazzPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", clazzPage.getTotalPages());
        return "/admin/admin-class-list";
    }

    // 显示添加班级页面
    @GetMapping("/classes/add")
    public String addClassForm(Model model) {
        model.addAttribute("clazz", new Clazz());
        return "/admin/admin-class-add-form";
    }

    // 保存新添加的班级信息
    @PostMapping("/classes/save")
    public String saveClass(@ModelAttribute Clazz clazz) {
        clazzService.saveClass(clazz);
        return "redirect:/admin/classes";
    }

    // 显示编辑班级页面
    @GetMapping("/classes/edit/{classCode}")
    public String editClassForm(@PathVariable String classCode, Model model) {
        Clazz clazz = clazzService.getByClassCode(classCode);
        model.addAttribute("clazz", clazz);
        return "/admin/admin-class-edit-form";
    }

    // 更新班级信息
    @PostMapping("/classes/update")
    public String updateClass(@ModelAttribute Clazz clazz) {
        clazzService.updateClass(clazz);
        return "redirect:/admin/classes";
    }

    // 删除班级信息
    @PostMapping("/classes/delete")
    public String deleteClass(@RequestParam List<String> classCodes) {
        clazzService.deleteByClassCodes(classCodes);
        return "redirect:/admin/classes";
    }

    // 显示课程列表页面，支持查询功能
    @GetMapping("/courses")
    public String listCourses(@RequestParam(required = false) String courseCode,
                              @RequestParam(required = false) String courseName,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {

        Page<Course> coursePage;
        if (courseCode != null && !courseCode.isEmpty()) {
            // 若课程编号存在，则仅按课程编号查询
            Course course = courseService.getByCourseCode(courseCode);
            if (course != null) {
                coursePage = new PageImpl<>(Collections.singletonList(course), PageRequest.of(page, size), 1);
            } else {
                coursePage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
            }
        } else if (courseName != null && !courseName.isEmpty()) {
            // 按课程名称查询
            coursePage = courseService.getByCourseNameContaining(courseName, PageRequest.of(page, size));
        } else {
            // 查询所有课程
            coursePage = courseService.getAllCourses(PageRequest.of(page, size));
        }

        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        return "/admin/admin-course-list";
    }

    // 显示添加课程页面
    @GetMapping("/courses/add")
    public String addCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "/admin/admin-course-add-form";
    }

    // 保存新添加的课程信息
    @PostMapping("/courses/save")
    public String saveCourse(@ModelAttribute Course course) {
        courseService.saveCourse(course);
        return "redirect:/admin/courses";
    }

    // 显示编辑课程页面
    @GetMapping("/courses/edit/{courseCode}")
    public String editCourseForm(@PathVariable String courseCode, Model model) {
        Course course = courseService.getByCourseCode(courseCode);
        model.addAttribute("course", course);
        return "/admin/admin-course-edit-form";
    }

    // 更新课程信息
    @PostMapping("/courses/update")
    public String updateCourse(@ModelAttribute Course course) {
        courseService.updateCourse(course);
        return "redirect:/admin/courses";
    }

    // 删除课程信息
    @PostMapping("/courses/delete")
    public String deleteCourse(@RequestParam List<String> courseCodes) {
        courseService.deleteByCourseCodes(courseCodes);
        return "redirect:/admin/courses";
    }
}
