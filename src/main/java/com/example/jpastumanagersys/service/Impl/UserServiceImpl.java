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

    // 管理员更新用户信息，更新用户密码无需输入旧的密码
    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findByUserCode(user.getUserCode()).orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        existingUser.setClazz(user.getClazz());

        return userRepository.save(existingUser);
    }

    // 根据用户编号删除用户
    @Override
    public void deleteUserByUserCode(String userCode) {
        userRepository.deleteUserByUserCode(userCode);
    }

    // 根据用户编号获取用户
    @Override
    public User getUserByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("没有找到用户"));
    }

    // 获取所有用户
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 用户更改密码
    @Override
    public User changePassword(String userCode, String oldPassword, String newPassword) {
        User user = userRepository.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("没有找到用户"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    // Spring Security自带的登录验证方法，实际为通过userCode进行验证登录，而非username
    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {
        User user = userRepository.findByUserCode(userCode).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).roles(user.getRole()).build();
    }

    // 根据条件获取用户
    @Override
    public Page<User> getStudentsByCondition(String userCode, String username, Long classId, Pageable pageable) {
        Page<User> allUsersPage;
        if (userCode != null && !userCode.isEmpty()) {
            // 用户编号是唯一的，获取的用户是唯一的，需要进行转换格式
            Optional<User> userOptional = userRepository.findByUserCode(userCode);
            if (userOptional.isPresent()) {
                List<User> userList = Collections.singletonList(userOptional.get());
                allUsersPage = new PageImpl<>(userList, pageable, userList.size());
            } else {
                allUsersPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            }
        } else if (username != null && classId != null) {
            allUsersPage = userRepository.findByUsernameContainingAndClazz_Id(username, classId, pageable);
        } else if (username != null) {
            allUsersPage = userRepository.findByUsernameContaining(username, pageable);
        } else if (classId != null) {
            allUsersPage = userRepository.findByClazz_Id(classId, pageable);
        } else {
            allUsersPage = userRepository.findAll(pageable);
        }

        // 过滤出角色为 STUDENT 的用户
        List<User> studentList = allUsersPage.getContent().stream()
                .filter(user -> "STUDENT".equals(user.getRole()))
                .collect(Collectors.toList());

        return new PageImpl<>(studentList, pageable, studentList.size());
    }

    @Override
    @Transactional
    public void deleteStudentsByUserCodes(List<String> userCodes) {
        userRepository.deleteAllByUserCodeIn(userCodes);
    }
}
