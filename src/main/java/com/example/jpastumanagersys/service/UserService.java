package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    // 保存用户信息
    User saveUser(User user);

    // 管理员更新用户信息
    User updateUser(User user);

    // 根据用户编号删除用户信息
    void deleteUserByUserCode(String userCode);

    // 根据用户编号获取用户
    User getUserByUserCode(String userCode);

    // 获取所有用户
    List<User> getAllUsers();

    // 用户更改密码
    User changePassword(String userCode, String oldPassword, String newPassword);

    // 根据条件获取用户信息
    Page<User> getStudentsByCondition(String userCode ,String name, Long clazzId, Pageable pageable);

    // 根据用户编号批量删除用户（暂时有问题）
    void deleteStudentsByUserCodes(List<String> userCodes);
}
