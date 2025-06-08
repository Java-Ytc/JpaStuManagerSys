package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.repo.UserRepo;
import com.example.jpastumanagersys.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 注册新用户
    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // 管理员更新学生信息，更新用户密码无需输入旧的密码
    @Override
    public User updateStudent(User user) {
        User existingUser = userRepository.findByUserCode(user.getUserCode()).orElseThrow(() -> new IllegalArgumentException("没有找到学生"));

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

        return userRepository.save(existingUser);
    }

    // 根据用户编号获取用户
    @Override
    public User getByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("无法根据编号获取用户"));
    }

    // 获取所有用户
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 用户更改密码
    @Override
    public User changePassword(String userCode, String oldPassword, String newPassword) {
        User user = userRepository.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("更改密码时没有找到用户"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    // Spring Security自带的登录验证方法，实际为通过userCode进行验证登录，而非username
    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {
        User user = userRepository.findByUserCode(userCode).orElseThrow(() -> new UsernameNotFoundException("登录时没有找到用户"));

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).roles(user.getRole()).build();
    }

    // 管理员根据条件获取学生
    @Override
    public Page<User> getStudentsByCondition(String userCode, String username, String clazzCode, Pageable pageable) {
        Page<User> allUsersPage;
        if (userCode != null && !userCode.isEmpty()) {
            System.out.println("执行学生编号查询");
            // 若学生编号存在，则仅按学生编号查询
            Optional<User> userOptional = userRepository.findByUserCode(userCode);
            if (userOptional.isPresent()) {
                List<User> userList = Collections.singletonList(userOptional.get());
                allUsersPage = new PageImpl<>(userList, pageable, userList.size());
            } else {
                allUsersPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            }

            System.out.println("学生编号查询到的用户数量：" + allUsersPage.getContent().size());
        } else if ((username != null && !username.isEmpty()) && (clazzCode != null && !clazzCode.isEmpty())) {
            System.out.println("执行联合查询");
            // 用户名和班级编号联合查询
            allUsersPage = userRepository.findByUsernameContainingAndClazz_ClassCode(username, clazzCode, pageable);
            System.out.println("联合查询到的用户数量：" + allUsersPage.getContent().size());
        } else if (username != null && !username.isEmpty()) {
            System.out.println("执行用户名查询");
            // 按用户名查询
            allUsersPage = userRepository.findByUsernameContaining(username, pageable);
            System.out.println("用户名查询到的用户数量：" + allUsersPage.getContent().size());
        } else if (clazzCode != null && !clazzCode.isEmpty()) {
            System.out.println("执行班级查询");
            // 按班级编号查询
            allUsersPage = userRepository.findByClazz_ClassCode(clazzCode, pageable);
            System.out.println("班级查询到的用户数量：" + allUsersPage.getContent().size());
        } else {
            // 查询所有学生
            allUsersPage = userRepository.findAll(pageable);
            System.out.println("查询所有学生的用户数量：" + allUsersPage.getContent().size());
        }

        // 过滤出角色为 STUDENT 的用户
        List<User> studentList = allUsersPage.getContent().stream()
                .filter(user -> "STUDENT".equals(user.getRole()))
                .collect(Collectors.toList());

        System.out.println("过滤后学生数量：" + studentList.size());

        return new PageImpl<>(studentList, pageable, studentList.size());
    }

    // 管理员根据条件批量删除学生
    @Override
    @Transactional
    public void deleteByUserCodes(List<String> userCodes) {
        userRepository.deleteAllByUserCodeIn(userCodes);
    }

    // 管理员根据条件获取教师
    @Override
    public Page<User> getTeachersByCondition(String userCode, String username, String courseCode, Pageable pageable) {
        Page<User> allUsersPage;
        if (userCode != null && !userCode.isEmpty()) {
            // 若教师编号存在，则仅按教师编号查询
            Optional<User> userOptional = userRepository.findByUserCode(userCode);
            if (userOptional.isPresent()) {
                List<User> userList = Collections.singletonList(userOptional.get());
                allUsersPage = new PageImpl<>(userList, pageable, userList.size());
            } else {
                allUsersPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            }
        } else if ((username != null && !username.isEmpty()) && (courseCode != null && !courseCode.isEmpty())) {
            // 用户名和课程编号联合查询
            allUsersPage = userRepository.findByUsernameContainingAndCourses_CourseCode(username, courseCode, pageable);
        } else if (username != null && !username.isEmpty()) {
            // 按用户名查询
            allUsersPage = userRepository.findByUsernameContaining(username, pageable);
        } else if (courseCode != null && !courseCode.isEmpty()) {
            // 按课程编号查询
            allUsersPage = userRepository.findByCourses_CourseCode(courseCode, pageable);
        } else {
            // 查询所有教师
            allUsersPage = userRepository.findAll(pageable);
        }

        // 过滤出角色为TEACHER的用户
        List<User> teacherList = allUsersPage.getContent().stream()
                .filter(user -> "TEACHER".equals(user.getRole()))
                .collect(Collectors.toList());

        return new PageImpl<>(teacherList, pageable, teacherList.size());
    }

    // 管理员更新教师信息
    @Override
    public User updateTeacher(User user) {
        User existingUser = userRepository.findByUserCode(user.getUserCode()).orElseThrow(() -> new IllegalArgumentException("没有找到教师"));

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

        return userRepository.save(existingUser);
    }

}
