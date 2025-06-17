package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.*;
import com.example.jpastumanagersys.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.jpastumanagersys.util.UserCodeUtils.getCurrentUserCode;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private LeaveApplicationService leaveApplicationService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CourseSelectionService selectionService;

    /**
     * 管理员主页
     * 该方法用于展示管理员主页信息，包含学生总数和教师总数。首先统计学生和教师的数量，
     * 然后将这些信息添加到模型中，最后返回管理员主页的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 管理员主页的视图名称
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        long totalStudents = userService.getAllUsers().stream().filter(user -> "STUDENT".equals(user.getRole())).count();

        long totalTeachers = userService.getAllUsers().stream().filter(user -> "TEACHER".equals(user.getRole())).count();

        String lastUpdateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalTeachers", totalTeachers);
        model.addAttribute("lastUpdateTime", lastUpdateTime);

        return "/admin/admin-dashboard";
    }


    /*------------------------------------------ 学生模块 ---------------------------------------*/

    /**
     * 显示学生列表页面
     * 该方法用于展示学生列表，支持根据学生编号、用户名和班级编号进行查询。
     * 根据传入的参数进行相应的查询，将查询结果添加到模型中，并返回学生列表页面的视图。
     *
     * @param userCode  学生编号
     * @param username  用户名
     * @param classCode 班级编号
     * @param page      当前页码，默认为 0
     * @param size      每页显示的记录数，默认为 10
     * @param model     用于传递数据到视图的模型对象
     * @return 学生列表页面的视图名称
     */
    @GetMapping("/students")
    public String listStudents(@RequestParam(required = false) String userCode, @RequestParam(required = false) String username, @RequestParam(required = false) String classCode, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {

        System.out.println("学生编号：" + userCode);
        System.out.println("用户名：" + username);
        System.out.println("接收到的班级编号：" + classCode);

        Page<User> studentPage;
        if (userCode != null && !userCode.isEmpty()) {
            // 若学生编号存在，则仅按学生编号查询
            studentPage = userService.getStudentsByCondition(userCode, null, null, PageRequest.of(page, size));
        } else {
            // 否则，根据用户名、班级编号或联合查询
            studentPage = userService.getStudentsByCondition(null, username, classCode, PageRequest.of(page, size));
        }

        System.out.println("查询到的学生数量：" + studentPage.getContent().size());

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        // 检查是否有学生数据
        if (studentPage.isEmpty()) {
            model.addAttribute("noStudentsMessage", "No students found.");
        }
        return "/admin/admin-student-list";
    }

    /**
     * 批量删除学生信息
     * 该方法用于处理批量删除学生信息的操作，根据传入的学生编号列表，调用用户服务删除相应的学生信息，
     * 然后重定向到学生列表页面。
     *
     * @param userCodes 学生编号列表
     * @return 重定向的 URL
     */
    @PostMapping("/students/delete")
    public String deleteStudents(@RequestParam List<String> userCodes) {
        userService.deleteStudentsByUserCodes(userCodes);
        return "redirect:/admin/students";
    }

    /**
     * 显示添加学生页面
     * 该方法用于展示添加学生的表单页面，将一个新的用户对象添加到模型中，然后返回添加学生页面的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 添加学生页面的视图名称
     */
    @GetMapping("/students/add")
    public String addStudentForm(Model model) {
        model.addAttribute("user", new User());
        return "/admin/admin-student-add-form";
    }

    /**
     * 保存新添加的学生信息
     * 该方法用于处理保存新添加学生信息的操作，将用户对象的角色设置为学生，然后调用用户服务保存该用户信息，
     * 最后重定向到学生列表页面。
     *
     * @param user 用户对象，包含新添加学生的信息
     * @return 重定向的 URL
     */
    @PostMapping("/students/save")
    public String saveStudent(@ModelAttribute User user) {
        user.setRole("STUDENT");
        userService.saveUser(user);
        return "redirect:/admin/students";
    }

    /**
     * 显示编辑学生页面
     * 该方法用于展示编辑学生信息的表单页面，根据学生编号获取学生信息，并将其添加到模型中，
     * 然后返回编辑学生页面的视图。
     *
     * @param userCode 学生编号
     * @param model    用于传递数据到视图的模型对象
     * @return 编辑学生页面的视图名称
     */
    @GetMapping("/students/edit/{userCode}")
    public String editStudentForm(@PathVariable String userCode, Model model) {
        User user = userService.getByUserCode(userCode);
        model.addAttribute("user", user);
        return "/admin/admin-student-edit-form";
    }

    /**
     * 更新学生信息
     * 该方法用于处理更新学生信息的操作，调用用户服务更新学生信息，然后重定向到学生列表页面。
     *
     * @param user 用户对象，包含要更新的学生信息
     * @return 重定向的 URL
     */
    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute User user) {
        System.out.println("接受到的userCode：" + user.getUserCode());
        userService.updateStudent(user);
        return "redirect:/admin/students";
    }


    /*----------------------------------------------- 教师模块 -------------------------------------------------*/

    /**
     * 显示教师列表
     * 该方法用于展示教师列表，支持根据教师编号和用户名进行查询。根据传入的参数进行相应的查询，
     * 将查询结果添加到模型中，并返回教师列表页面的视图。
     *
     * @param userCode 教师编号
     * @param username 用户名
     * @param page     当前页码，默认为 0
     * @param size     每页显示的记录数，默认为 10
     * @param model    用于传递数据到视图的模型对象
     * @return 教师列表页面的视图名称
     */
    @GetMapping("/teachers")
    public String listTeachers(@RequestParam(required = false) String userCode, @RequestParam(required = false) String username, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        System.out.println("用户编号" + userCode);
        System.out.println("用户名" + username);
        Page<User> teacherPage;
        if (userCode != null && !userCode.isEmpty()) {
            // 若教师编号存在，则仅按教师编号查询
            teacherPage = userService.getTeachersByCondition(userCode, null, null, PageRequest.of(page, size));
        } else if (username != null && !username.isEmpty()) {
            // 按用户名查询
            teacherPage = userService.getTeachersByCondition(null, username, null, PageRequest.of(page, size));
        } else {
            // 查询所有教师
            teacherPage = userService.getTeachersByCondition(null, null, null, PageRequest.of(page, size));
        }
        List<Boolean> hasTeachingList = new ArrayList<>();
        for (User teacher : teacherPage.getContent()) {
            hasTeachingList.add(userService.hasTeachingCourses(teacher.getUserCode()));
        }
        model.addAttribute("teachers", teacherPage.getContent());
        model.addAttribute("hasTeachingList", hasTeachingList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", teacherPage.getTotalPages());
        // 检查是否有教师数据
        if (teacherPage.isEmpty()) {
            model.addAttribute("noTeachersMessage", "没有老师数据");
        }
        return "/admin/admin-teacher-list";
    }

    /**
     * 显示添加教师页面
     * 该方法用于展示添加教师的表单页面，将一个新的用户对象添加到模型中，然后返回添加教师页面的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 添加教师页面的视图名称
     */
    @GetMapping("/teachers/add")
    public String addTeacherForm(Model model) {
        model.addAttribute("user", new User());
        return "/admin/admin-teacher-add-form";
    }

    /**
     * 保存新添加的教师信息
     * 该方法用于处理保存新添加教师信息的操作，将用户对象的角色设置为教师，然后调用用户服务保存该用户信息，
     * 最后重定向到教师列表页面。
     *
     * @param user 用户对象，包含新添加教师的信息
     * @return 重定向的 URL
     */
    @PostMapping("/teachers/save")
    public String saveTeacher(@ModelAttribute User user) {
        user.setRole("TEACHER");
        userService.saveUser(user);
        return "redirect:/admin/teachers";
    }

    /**
     * 显示编辑教师页面
     * 该方法用于展示编辑教师信息的表单页面，根据教师编号获取教师信息，并将其添加到模型中，
     * 然后返回编辑教师页面的视图。
     *
     * @param userCode 教师编号
     * @param model    用于传递数据到视图的模型对象
     * @return 编辑教师页面的视图名称
     */
    @GetMapping("/teachers/edit/{userCode}")
    public String editTeacherForm(@PathVariable String userCode, Model model) {
        User user = userService.getByUserCode(userCode);
        model.addAttribute("user", user);
        return "/admin/admin-teacher-edit-form";
    }

    /**
     * 更新教师信息
     * 该方法用于处理更新教师信息的操作，调用用户服务更新教师信息，然后重定向到教师列表页面。
     *
     * @param user 用户对象，包含要更新的教师信息
     * @return 重定向的 URL
     */
    @PostMapping("/teachers/update")
    public String updateTeacher(@ModelAttribute User user) {
        userService.updateTeacher(user);
        return "redirect:/admin/teachers";
    }

    /**
     * 批量删除教师信息
     * 该方法用于处理批量删除教师信息的操作，根据传入的教师编号列表，调用用户服务删除相应的教师信息，
     * 然后重定向到教师列表页面。
     *
     * @param userCodes 教师编号列表
     * @return 重定向的 URL
     */
    @PostMapping("/teachers/delete")
    public String deleteTeachers(@RequestParam List<String> userCodes, Model model) {
        List<String> teachersWithCourses = new ArrayList<>();
        for (String userCode : userCodes) {
            if (userService.hasTeachingCourses(userCode)) {
                User teacher = userService.getByUserCode(userCode);
                teachersWithCourses.add(teacher.getUsername());
            }
        }
        if (!teachersWithCourses.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("当前教师 ");
            for (String teacherName : teachersWithCourses) {
                errorMessage.append(teacherName).append(" ");
            }
            errorMessage.append("有授课，请先解除授课");
            model.addAttribute("deleteError", errorMessage.toString());
            return listTeachers(null, null, 0, 10, model);
        }
        userService.deleteTeachersByUserCodes(userCodes);
        return "redirect:/admin/teachers";
    }

    /**
     * 显示教师详细信息页面
     * 该方法用于展示教师的详细信息和所教授的课程信息。根据教师编号获取教师信息和该教师教授的课程，
     * 将这些信息添加到模型中，然后返回教师详细信息页面的视图。
     *
     * @param userCode 教师编号
     * @param page     当前页码，默认为 0
     * @param size     每页显示的记录数，默认为 10
     * @param model    用于传递数据到视图的模型对象
     * @return 教师详细信息页面的视图名称
     */
    @GetMapping("/teachers/detail/{userCode}")
    public String showTeacherDetails(@PathVariable String userCode, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        User teacher = userService.getByUserCode(userCode);
        Page<Course> courses = courseService.getByTeacher(teacher, PageRequest.of(page, size)); // 获取该教师教授的课程

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        return "/admin/admin-teacher-detail";
    }

    /**
     * 显示为老师分配课程的页面
     * 该方法用于展示为老师分配课程的页面，根据教师编号获取教师信息和未分配的课程，
     * 将这些信息添加到模型中，然后返回分配课程页面的视图。
     *
     * @param userCode 教师编号
     * @param page     当前页码，默认为 0
     * @param size     每页显示的记录数，默认为 10
     * @param model    用于传递数据到视图的模型对象
     * @return 分配课程页面的视图名称
     */
    @GetMapping("/teachers/{userCode}/assign-courses")
    public String showAssignCoursesPage(@PathVariable String userCode, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        // 获取老师信息
        User teacher = userService.getByUserCode(userCode);
        model.addAttribute("teacher", teacher);

        // 获取未分配的课程，支持分页查询
        Page<Course> unassignedCourses = courseService.getUnassignedCourses(PageRequest.of(page, size));
        model.addAttribute("unassignedCourses", unassignedCourses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", unassignedCourses.getTotalPages());

        return "/admin/admin-teacher-assign-courses";
    }

    /**
     * 处理单个课程分配请求
     * 该方法用于处理为老师分配单个课程的请求，根据教师编号和课程编号，调用用户服务进行课程分配，
     * 然后重定向到教师详细信息页面。
     *
     * @param userCode   教师编号
     * @param courseCode 课程编号
     * @return 重定向的 URL
     */
    @PostMapping("/teachers/{userCode}/assign-courses-single")
    public String assignSingleCourseToTeacher(@PathVariable String userCode, @RequestParam String courseCode) {
        userService.assignCoursesToTeacher(userCode, Collections.singletonList(courseCode));
        return "redirect:/admin/teachers/detail/" + userCode;
    }

    /**
     * 处理批量课程分配请求
     * 该方法用于处理为老师分配多个课程的请求，根据教师编号和课程编号列表，调用用户服务进行课程分配，
     * 然后重定向到教师详细信息页面。
     *
     * @param userCode    教师编号
     * @param courseCodes 课程编号列表
     * @return 重定向的 URL
     */
    @PostMapping("/teachers/{userCode}/assign-courses-batch")
    public String assignCoursesToTeacher(@PathVariable String userCode, @RequestParam List<String> courseCodes) {
        userService.assignCoursesToTeacher(userCode, courseCodes);
        return "redirect:/admin/teachers/detail/" + userCode;
    }

    /**
     * 解除教师和课程关联
     * 该方法用于处理解除教师和课程关联的请求，根据教师编号和课程编号列表，调用用户服务解除关联，
     * 然后重定向到教师详细信息页面。
     *
     * @param userCode    教师编号
     * @param courseCodes 课程编号列表
     * @return 重定向的 URL
     */
    @PostMapping("/teachers/{userCode}/dissociate-courses")
    public String dissociateCourses(@PathVariable String userCode, @RequestParam List<String> courseCodes) {
        userService.dissociateCoursesFromTeacher(userCode, courseCodes);
        return "redirect:/admin/teachers/detail/" + userCode;
    }


    /*------------------------------------------------ 班级模块 ------------------------------------------*/

    /**
     * 显示班级列表页面，支持查询功能
     * 该方法用于展示班级列表，支持根据班级编号和班级名称进行查询。根据传入的参数进行相应的查询，
     * 将查询结果添加到模型中，并返回班级列表页面的视图。
     *
     * @param classCode 班级编号
     * @param className 班级名称
     * @param page      当前页码，默认为 0
     * @param size      每页显示的记录数，默认为 10
     * @param model     用于传递数据到视图的模型对象
     * @return 班级列表页面的视图名称
     */
    @GetMapping("/classes")
    public String listClasses(@RequestParam(required = false) String classCode, @RequestParam(required = false) String className, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {

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

    /**
     * 显示添加班级页面
     * 该方法用于展示添加班级的表单页面，将一个新的班级对象添加到模型中，然后返回添加班级页面的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 添加班级页面的视图名称
     */
    @GetMapping("/classes/add")
    public String addClassForm(Model model) {
        model.addAttribute("clazz", new Clazz());
        return "/admin/admin-class-add-form";
    }

    /**
     * 保存新添加的班级信息
     * 该方法用于处理保存新添加班级信息的操作，调用班级服务保存该班级信息，然后重定向到班级列表页面。
     *
     * @param clazz 班级对象，包含新添加班级的信息
     * @return 重定向的 URL
     */
    @PostMapping("/classes/save")
    public String saveClass(@ModelAttribute Clazz clazz) {
        clazzService.saveClass(clazz);
        return "redirect:/admin/classes";
    }

    /**
     * 显示编辑班级页面
     * 该方法用于展示编辑班级信息的表单页面，根据班级编号获取班级信息，并将其添加到模型中，
     * 然后返回编辑班级页面的视图。
     *
     * @param classCode 班级编号
     * @param model     用于传递数据到视图的模型对象
     * @return 编辑班级页面的视图名称
     */
    @GetMapping("/classes/edit/{classCode}")
    public String editClassForm(@PathVariable String classCode, Model model) {
        Clazz clazz = clazzService.getByClassCode(classCode);
        model.addAttribute("clazz", clazz);
        return "/admin/admin-class-edit-form";
    }

    /**
     * 更新班级信息
     * 该方法用于处理更新班级信息的操作，调用班级服务更新班级信息，然后重定向到班级列表页面。
     *
     * @param clazz 班级对象，包含要更新的班级信息
     * @return 重定向的 URL
     */
    @PostMapping("/classes/update")
    public String updateClass(@ModelAttribute Clazz clazz) {
        clazzService.updateClass(clazz);
        return "redirect:/admin/classes";
    }

    /**
     * 删除班级信息
     * 该方法用于处理删除班级信息的操作，根据传入的班级编号列表，调用班级服务删除相应的班级信息，
     * 然后重定向到班级列表页面。
     *
     * @param classCodes 班级编号列表
     * @return 重定向的 URL
     */
    @PostMapping("/classes/delete")
    public String deleteClass(@RequestParam List<String> classCodes) {
        clazzService.deleteByClassCodes(classCodes);
        return "redirect:/admin/classes";
    }

    /**
     * 显示为班级分配学生的页面
     * 该方法用于展示为班级分配学生的页面，根据班级编号获取班级信息和未分配的学生，
     * 将这些信息添加到模型中，然后返回分配学生页面的视图。
     *
     * @param classCode 班级编号
     * @param page      当前页码，默认为 0
     * @param size      每页显示的记录数，默认为 10
     * @param model     用于传递数据到视图的模型对象
     * @return 分配学生页面的视图名称
     */
    @GetMapping("/classes/{classCode}/assign-students")
    public String showAssignStudentsPage(@PathVariable String classCode, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Clazz clazz = clazzService.getByClassCode(classCode);
        Page<User> unassignedStudents = userService.getUnassignedStudents(PageRequest.of(page, size));
        model.addAttribute("clazz", clazz);
        model.addAttribute("unassignedStudents", unassignedStudents.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", unassignedStudents.getTotalPages());
        return "/admin/admin-class-assign-students";
    }

    /**
     * 处理为班级批量分配学生的请求
     * 该方法用于处理为班级分配多个学生的请求，根据班级编号和学生编号列表，调用用户服务进行学生分配，
     * 然后重定向到班级列表页面。
     *
     * @param classCode 班级编号
     * @param userCodes 学生编号列表
     * @return 重定向的 URL
     */
    @PostMapping("/classes/{classCode}/assign-students")
    public String assignStudentsToClass(@PathVariable String classCode, @RequestParam List<String> userCodes) {
        userService.assignStudentsToClass(classCode, userCodes);
        return "redirect:/admin/classes";
    }


    /*-------------------------------------------- 课程模块 --------------------------------------------*/

    /**
     * 显示课程列表页面，支持查询功能
     * 该方法用于展示课程列表，支持根据课程编号和课程名称进行查询。根据传入的参数进行相应的查询，
     * 将查询结果添加到模型中，并返回课程列表页面的视图。
     *
     * @param courseCode 课程编号
     * @param courseName 课程名称
     * @param page       当前页码，默认为 0
     * @param size       每页显示的记录数，默认为 10
     * @param model      用于传递数据到视图的模型对象
     * @return 课程列表页面的视图名称
     */
    @GetMapping("/courses")
    public String listCourses(@RequestParam(required = false) String courseCode, @RequestParam(required = false) String courseName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {

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

    /**
     * 显示添加课程页面
     * 该方法用于展示添加课程的表单页面，将一个新的课程对象添加到模型中，然后返回添加课程页面的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 添加课程页面的视图名称
     */
    @GetMapping("/courses/add")
    public String addCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "/admin/admin-course-add-form";
    }

    /**
     * 保存新添加的课程信息
     * 该方法用于处理保存新添加课程信息的操作，将课程的当前学生数设置为 0，然后调用课程服务保存该课程信息，
     * 最后重定向到课程列表页面。
     *
     * @param course 课程对象，包含新添加课程的信息
     * @return 重定向的 URL
     */
    @PostMapping("/courses/save")
    public String saveCourse(@ModelAttribute Course course) {
        course.setCurrentStudents(0); // 设置默认当前学生数为0
        courseService.saveCourse(course);
        return "redirect:/admin/courses";
    }

    /**
     * 显示编辑课程页面
     * 该方法用于展示编辑课程信息的表单页面，根据课程编号获取课程信息，并将其添加到模型中，
     * 然后返回编辑课程页面的视图。
     *
     * @param courseCode 课程编号
     * @param model      用于传递数据到视图的模型对象
     * @return 编辑课程页面的视图名称
     */
    @GetMapping("/courses/edit/{courseCode}")
    public String editCourseForm(@PathVariable String courseCode, Model model) {
        Course course = courseService.getByCourseCode(courseCode);
        model.addAttribute("course", course);
        return "/admin/admin-course-edit-form";
    }

    /**
     * 更新课程信息
     * 该方法用于处理更新课程信息的操作，调用课程服务更新课程信息，然后重定向到课程列表页面。
     *
     * @param course 课程对象，包含要更新的课程信息
     * @return 重定向的 URL
     */
    @PostMapping("/courses/update")
    public String updateCourse(@ModelAttribute Course course) {
        courseService.updateCourse(course);
        return "redirect:/admin/courses";
    }

    /**
     * 删除课程信息
     * 该方法用于处理删除课程信息的操作，根据传入的课程编号列表，调用课程服务删除相应的课程信息，
     * 然后重定向到课程列表页面。
     *
     * @param courseCodes 课程编号列表
     * @return 重定向的 URL
     */
    @PostMapping("/courses/delete")
    public String deleteCourse(@RequestParam List<String> courseCodes) {
        courseService.deleteByCourseCodes(courseCodes);
        return "redirect:/admin/courses";
    }


    /*---------------------------------------- 请假审批 ------------------------------------*/

    /**
     * 显示待审批的请假申请列表
     * 该方法用于展示待审批的请假申请列表，获取所有待审批的请假申请，并将其添加到模型中，
     * 然后返回待审批请假申请列表页面的视图。
     *
     * @param model 用于传递数据到视图的模型对象
     * @return 待审批请假申请列表页面的视图名称
     */
    @GetMapping("/leave-applications/pending")
    public String showPendingLeaveApplications(Model model) {
        List<LeaveApplication> pendingApplications = leaveApplicationService.getPendingLeaveApplications();
        model.addAttribute("pendingApplications", pendingApplications);
        return "/admin/admin-pending-leave-applications";
    }

    /**
     * 批准请假申请
     * 该方法用于处理批准请假申请的操作，根据请假申请 ID 和当前用户的编号获取审批人 ID，
     * 调用请假服务批准请假申请。如果操作成功，添加成功消息到模型中；如果出现异常，添加异常信息到模型中，
     * 最后重定向到待审批请假申请列表页面。
     *
     * @param leaveId 请假申请 ID
     * @param model   用于传递数据到视图的模型对象
     * @return 重定向的 URL
     */
    @PostMapping("/leave-applications/{leaveId}/approve")
    public String approveLeave(@PathVariable Long leaveId, Model model) {
        String approverCode = getCurrentUserCode();
        Long approverId = userService.getByUserCode(approverCode).getId();
        try {
            leaveApplicationService.approveLeave(leaveId, approverId);
            model.addAttribute("message", "请假申请已批准");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/leave-applications/pending";
    }

    /**
     * 拒绝请假申请
     * 该方法用于处理拒绝请假申请的操作，根据请假申请 ID 和当前用户的编号获取审批人 ID，
     * 调用请假服务拒绝请假申请。如果操作成功，添加成功消息到模型中；如果出现异常，添加异常信息到模型中，
     * 最后重定向到待审批请假申请列表页面。
     *
     * @param leaveId 请假申请 ID
     * @param model   用于传递数据到视图的模型对象
     * @return 重定向的 URL
     */
    @PostMapping("/leave-applications/{leaveId}/reject")
    public String rejectLeave(@PathVariable Long leaveId, Model model) {
        String approverCode = getCurrentUserCode();
        Long approverId = userService.getByUserCode(approverCode).getId();
        try {
            leaveApplicationService.rejectLeave(leaveId, approverId);
            model.addAttribute("message", "请假申请已拒绝");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/leave-applications/pending";
    }


    /*----------------------------------------- 成绩管理 -------------------------------------*/
    /**
     * 查看成绩列表
     * 该方法用于根据课程编号、课程名称、学生编号和学生名称等条件分页查询成绩信息。
     * 调用成绩服务的 getScoresByCondition 方法进行查询，并将查询结果添加到模型中，
     * 最后返回成绩列表页面的视图。
     *
     * @param courseCode  课程编号，可选参数
     * @param courseName  课程名称，可选参数
     * @param studentCode 学生编号，可选参数
     * @param studentName 学生名称，可选参数
     * @param page        当前页码，默认值为 0
     * @param size        每页显示的记录数，默认值为 10
     * @param model       用于传递数据到视图的模型对象
     * @return 成绩列表页面的视图名称
     */
    @GetMapping("/scores")
    public String listScores(@RequestParam(required = false) String courseCode, @RequestParam(required = false) String courseName, @RequestParam(required = false) String studentCode, @RequestParam(required = false) String studentName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {

        Page<Score> scorePage = scoreService.getScoresByCondition(courseCode, courseName, studentCode, studentName, PageRequest.of(page, size));

        model.addAttribute("scores", scorePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", scorePage.getTotalPages());
        if (scorePage.isEmpty()) {
            model.addAttribute("noScoresMessage", "No scores found.");
        }
        return "/admin/admin-score-list";
    }

    /**
     * 显示添加成绩表单页面
     * 该方法用于根据课程编号、课程名称、学生编号和学生名称等条件分页查询选课记录信息。
     * 对于每个选课记录，查询对应的成绩信息并设置到选课记录中，然后将查询结果添加到模型中，
     * 最后返回添加成绩表单页面的视图。
     *
     * @param courseCode  课程编号，可选参数
     * @param courseName  课程名称，可选参数
     * @param studentCode 学生编号，可选参数
     * @param studentName 学生名称，可选参数
     * @param page        当前页码，默认值为 0
     * @param size        每页显示的记录数，默认值为 10
     * @param model       用于传递数据到视图的模型对象
     * @return 添加成绩表单页面的视图名称
     */
    @GetMapping("/scores/edit")
    public String editScoreForm(@RequestParam(required = false) String courseCode, @RequestParam(required = false) String courseName, @RequestParam(required = false) String studentCode, @RequestParam(required = false) String studentName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {

        Page<CourseSelection> selectionPage = selectionService.getSelectionsByCondition(courseCode, courseName, studentCode, studentName, PageRequest.of(page, size));

        for (CourseSelection selection : selectionPage.getContent()) {
            User student = selection.getStudent();
            Course course = selection.getCourse();
            Score score = scoreService.getByStudentAndCourse(student.getId(), course.getId());
            if (score != null) {
                selection.setScore(score.getScore());
            }
        }

        model.addAttribute("selections", selectionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", selectionPage.getTotalPages());
        if (selectionPage.isEmpty()) {
            model.addAttribute("noSelectionsMessage", "No course selections found.");
        }
        return "/admin/admin-score-edit-form";
    }

    /**
     * 保存成绩信息
     * 该方法用于处理保存成绩的操作，根据选课记录 ID 和分数，查询对应的学生和课程信息，
     * 然后更新或创建成绩记录，并将成绩信息保存到数据库中。同时更新选课记录中的成绩信息，
     * 最后重定向到成绩列表页面。
     *
     * @param selectionId 选课记录 ID
     * @param score       分数
     * @return 重定向的 URL
     */
    @PostMapping("/scores/save")
    public String saveScore(@RequestParam Long selectionId, @RequestParam Double score) {
        CourseSelection selection = selectionService.getById(selectionId);
        User student = selection.getStudent();
        Course course = selection.getCourse();

        Score scoreObj = scoreService.getByStudentAndCourse(student.getId(), course.getId());
        if (scoreObj == null) {
            scoreObj = new Score();
            scoreObj.setStudent(student);
            scoreObj.setCourse(course);
        }
        scoreObj.setScore(score);
        scoreService.save(scoreObj);

        selection.setScore(score);
        selectionService.updateScore(selectionId, score);

        return "redirect:/admin/scores";
    }
}