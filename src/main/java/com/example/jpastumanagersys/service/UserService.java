package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    User getUserById(Long id);

    List<User> getAllUsers();

    User changePassword(Long id, String oldPassword, String newPassword);
}
