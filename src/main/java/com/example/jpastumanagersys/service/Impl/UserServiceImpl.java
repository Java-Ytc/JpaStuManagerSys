package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.config.CustomUserDetails;
import com.example.jpastumanagersys.entity.Clazz;
import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.repo.ClazzRepo;
import com.example.jpastumanagersys.repo.CourseRepo;
import com.example.jpastumanagersys.repo.UserRepo;
import com.example.jpastumanagersys.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClazzRepo clazzRepo;

    @Autowired
    private CourseRepo courseRepo;

    // 注册新用户
    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    // 管理员更新学生信息，更新用户密码无需输入旧的密码
    @Override
    public User updateStudent(User user) {
        User existingUser = userRepo.findByUserCode(user.getUserCode()).orElseThrow(() -> new IllegalArgumentException("没有找到学生"));

        existingUser.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        // 检查新角色是否为空，若为空则使用原角色
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            existingUser.setRole(user.getRole());
        }

        existingUser.setClazz(user.getClazz());

        return userRepo.save(existingUser);
    }

    // 根据用户编号获取用户
    @Override
    public User getByUserCode(String userCode) {
        return userRepo.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("无法根据编号获取用户"));
    }

    // 获取所有用户
    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // 管理员根据学生编号，批量删除
    @Override
    @Transactional
    public void deleteStudentsByUserCodes(List<String> userCodes) {
        for (String userCode : userCodes) {
            User user = userRepo.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("无法根据编号获取用户"));
            Clazz clazz = user.getClazz();
            if (clazz != null) {
                clazz.getStudents().remove(user); // 从班级中移除学生
                clazzRepo.save(clazz); // 保存班级信息
            }
        }
        userRepo.deleteAllByUserCodeIn(userCodes);
    }

    // 管理员批量删除教师信息
    @Override
    @Transactional
    public void deleteTeachersByUserCodes(List<String> userCodes) {
        for (String userCode : userCodes) {
            User user = userRepo.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("无法根据编号获取用户"));
            List<Course> courses = user.getCourses();
            if (courses != null && !courses.isEmpty()) {
                throw new IllegalStateException("教师 " + user.getUsername() + " 有相关授课，无法删除");
            }
        }
        userRepo.deleteAllByUserCodeIn(userCodes);
    }


    // 用户更改密码
    @Override
    public User changePassword(String userCode, String oldPassword, String newPassword) {
        User user = userRepo.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("更改密码时没有找到用户"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepo.save(user);
    }

    // Spring Security自带的登录验证方法，实际为通过userCode进行验证登录，而非username
    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {
        User user = userRepo.findByUserCode(userCode).orElseThrow(() -> new UsernameNotFoundException("登录时没有找到用户"));
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return new CustomUserDetails(user.getUsername(), user.getPassword(), user.getUserCode(), authorities);
    }

    // 管理员根据条件获取学生
    @Override
    public Page<User> getStudentsByCondition(String userCode, String username, String clazzCode, Pageable pageable) {
        List<User> allStudents;
        if (userCode != null && !userCode.isEmpty()) {
            System.out.println("执行学生编号查询");
            // 若学生编号存在，则仅按学生编号查询
            Optional<User> userOptional = userRepo.findByUserCode(userCode);
            allStudents = userOptional.map(List::of).orElse(List.of());
        } else if ((username != null && !username.isEmpty()) && (clazzCode != null && !clazzCode.isEmpty())) {
            System.out.println("执行联合查询");
            // 用户名和班级编号联合查询
            allStudents = userRepo.findByUsernameContainingAndClazz_ClassCode(username, clazzCode);
        } else if (username != null && !username.isEmpty()) {
            System.out.println("执行用户名查询");
            // 按用户名查询
            allStudents = userRepo.findByUsernameContaining(username);
        } else if (clazzCode != null && !clazzCode.isEmpty()) {
            System.out.println("执行班级查询");
            // 按班级编号查询
            allStudents = userRepo.findByClazz_ClassCode(clazzCode);
        } else {
            // 查询所有学生
            allStudents = userRepo.findAll();
        }

        // 过滤出角色为 STUDENT 的用户
        List<User> studentList = filterUsersByRole(allStudents, "STUDENT");

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), studentList.size());

        return new PageImpl<>(studentList.subList(start, end), pageable, studentList.size());
    }

    // 管理员根据条件获取教师
    @Override
    public Page<User> getTeachersByCondition(String userCode, String username, String courseCode, Pageable pageable) {
        List<User> allTeachers;
        if (userCode != null && !userCode.isEmpty()) {
            // 若教师编号存在，则仅按教师编号查询
            Optional<User> userOptional = userRepo.findByUserCode(userCode);
            allTeachers = userOptional.map(List::of).orElse(List.of());
        } else if (username != null && !username.isEmpty()) {
            // 按用户名查询
            allTeachers = userRepo.findByUsernameContaining(username);
        } else {
            // 查询所有教师
            allTeachers = userRepo.findAll();
        }

        // 过滤出角色为TEACHER的用户
        List<User> teacherList = filterUsersByRole(allTeachers, "TEACHER");

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), teacherList.size());

        return new PageImpl<>(teacherList.subList(start, end), pageable, teacherList.size());
    }


    // 管理员更新教师信息
    @Override
    public User updateTeacher(User user) {
        User existingUser = userRepo.findByUserCode(user.getUserCode()).orElseThrow(() -> new IllegalArgumentException("没有找到教师"));

        existingUser.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        // 检查新角色是否为空，若为空则使用原角色
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            existingUser.setRole(user.getRole());
        }

        return userRepo.save(existingUser);
    }

    @Override
    public Page<User> getUnassignedStudents(Pageable pageable) {
        // 先查询出所有未分配班级的用户
        List<User> allUnassignedUsers = userRepo.findByClazzIsNull();
        // 过滤出角色为 STUDENT 的用户
        List<User> unassignedStudents = filterUsersByRole(allUnassignedUsers, "STUDENT");

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), unassignedStudents.size());

        return new PageImpl<>(unassignedStudents.subList(start, end), pageable, unassignedStudents.size());
    }


    @Override
    @Transactional
    public void assignStudentsToClass(String classCode, List<String> userCodes) {
        // 1. 根据班级编号查找对应的班级
        Clazz clazz = clazzRepo.findByClassCode(classCode).orElseThrow(() -> new IllegalArgumentException("未找到对应的班级"));

        // 2. 根据学生编号列表查找对应的学生，并过滤出角色为 STUDENT 的用户
        List<User> students = userCodes.stream()
                .map(userCode -> userRepo.findByUserCode(userCode)
                        .filter(user -> "STUDENT".equals(user.getRole()))
                        .orElseThrow(() -> new IllegalArgumentException("未找到对应的学生或用户不是学生角色")))
                .toList();

        // 3. 遍历学生列表，将每个学生分配到指定班级
        for (User student : students) {
            student.setClazz(clazz);
            userRepo.save(student);
        }

        // 4. 更新班级的学生列表
        List<User> currentStudents = clazz.getStudents();
        if (currentStudents == null) {
            currentStudents = new ArrayList<>();
        }
        currentStudents.addAll(students);
        clazz.setStudents(currentStudents);
        clazzRepo.save(clazz);
    }

    @Override
    @Transactional
    public void assignCoursesToTeacher(String userCode, List<String> courseCodes) {
        User teacher = userRepo.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("没有找到教师"));
        for (String courseCode : courseCodes) {
            Course course = courseRepo.findByCourseCode(courseCode).orElseThrow(() -> new IllegalArgumentException("没有找到课程"));
            course.setTeacher(teacher);
            teacher.getCourses().add(course);
            courseRepo.save(course);
        }
        userRepo.save(teacher);
    }

    @Override
    @Transactional
    public void dissociateCoursesFromTeacher(String userCode, List<String> courseCodes) {
        User teacher = userRepo.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("没有找到教师"));

        for (String courseCode : courseCodes) {
            Course course = courseRepo.findByCourseCode(courseCode).orElseThrow(() -> new IllegalArgumentException("没有找到课程"));
            teacher.getCourses().remove(course);
            course.setTeacher(null);
            courseRepo.save(course);
        }

        userRepo.save(teacher);
    }

    @Override
    public List<User> filterUsersByRole(List<User> users, String role) {
        return users.stream()
                .filter(user -> role.equals(user.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasTeachingCourses(String userCode) {
        User teacher = getByUserCode(userCode);
        List<Course> courses = courseRepo.findByTeacher(teacher);
        return !courses.isEmpty();
    }
}
