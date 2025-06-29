package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    // 保存用户信息
    User saveUser(User user);

    // 用户更改密码
    User changePassword(String userCode, String oldPassword, String newPassword);

    // 获取所有用户
    List<User> getAllUsers();

    // 根据用户编号获取用户
    User getByUserCode(String userCode);

    // 管理员根据学生编号，批量删除
    void deleteStudentsByUserCodes(List<String> userCodes);

    // 管理员根据老师编号，批量删除
    void deleteTeachersByUserCodes(List<String> userCodes);

    // 管理员更新学生信息
    User updateStudent(User user);

    // 管理员更新教师信息
    User updateTeacher(User user);

    // 根据条件获取学生信息
    Page<User> getStudentsByCondition(String userCode, String name, String clazzCode, Pageable pageable);

    // 根据条件获取教师信息
    Page<User> getTeachersByCondition(String userCode, String name, String courseCode, Pageable pageable);

    // 获取未分配班级的学生
    Page<User> getUnassignedStudents(Pageable pageable);

    // 为班级批量分配学生
    void assignStudentsToClass(String userCode, List<String> classCodes);

    // 为老师分配课程
    void assignCoursesToTeacher(String userCode, List<String> courseCodes);

    // 批量解除课程与教师的关联
    void dissociateCoursesFromTeacher(String userCode, List<String> courseCodes);

    // 根据角色过滤用户列表
    List<User> filterUsersByRole(List<User> users, String role);

    // 判断教师是否有授课
    boolean hasTeachingCourses(String userCode);
}
