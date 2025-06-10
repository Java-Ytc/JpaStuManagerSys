package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    // 根据用户编号获取用户信息
    Optional<User> findByUserCode(String userCode);

    // 按姓名模糊查询学生信息
    List<User> findByUsernameContaining(String username);

    // 根据教室编号查询学生信息
    List<User> findByClazz_ClassCode(String clazzClassCode);

    // 按用户名和班级编号 联合查询学生信息
    List<User> findByUsernameContainingAndClazz_ClassCode(String username, String classCode);

    // 根据用户编号批量删除用户信息
    void deleteAllByUserCodeIn(List<String> userCodes);

    // 根据课程编号查询教师信息
    List<User> findByCourses_CourseCode(String courseCode);

    // 查询为分配班级的学生
    List<User> findByClazzIsNull();
}
