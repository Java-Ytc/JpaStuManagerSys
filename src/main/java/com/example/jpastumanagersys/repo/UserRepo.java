package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    // 根据用户编号获取用户信息
    Optional<User> findByUserCode(String userCode);

    // 按姓名模糊查询学生信息
    Page<User> findByUsernameContaining(String username, Pageable pageable);

    // 根据教室编号查询学生信息
    Page<User> findByClazz_ClassCode(String clazzClassCode, Pageable pageable);

    // 按用户名和班级编号 联合查询学生信息
    Page<User> findByUsernameContainingAndClazz_ClassCode(String username, String classCode, Pageable pageable);

    // 根据用户编号批量删除用户信息
    void deleteAllByUserCodeIn(List<String> userCodes);

    // 根据课程编号查询教师信息
    Page<User> findByCourses_CourseCode(String courseCode, Pageable pageable);

    // 根据用户名和课程编号 联合模糊查询教师信息
    Page<User> findByUsernameContainingAndCourses_CourseCode(String username, String courseCode, Pageable pageable);

    // 查询为分配班级的学生
    Page<User> findByClazzIsNull(Pageable pageable);
}
