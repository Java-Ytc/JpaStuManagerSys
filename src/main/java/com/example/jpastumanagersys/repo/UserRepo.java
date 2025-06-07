package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    // 按姓名模糊查询学生信息
    Page<User> findByUsernameContaining(String name, Pageable pageable);

    // 按班级 ID 查询学生信息
    Page<User> findByClazz_Id(Long classId, Pageable pageable);

    // 按姓名和班级 ID 联合查询学生信息
    Page<User> findByUsernameContainingAndClazz_Id(String name, Long classId, Pageable pageable);

    // 按照姓名删除删除用户信息
    void deleteByUsername(String username);

    // 按照编号删除用户信息
    void deleteUserByUserCode(String userCode);

    // 根据用户编号获取用户信息
    Optional<User> findByUserCode(String userCode);

    void deleteAllByUserCodeIn(List<String> userCodes);
}
