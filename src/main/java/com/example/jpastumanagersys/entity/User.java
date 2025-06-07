package com.example.jpastumanagersys.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;
    private String email;
    private String phone;

    @Column(unique = true)
    private String userCode;

    @PrePersist
    public void generateUserCode() {
        if (userCode == null) {
            // 生成一个唯一的6位数编号
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            userCode = uuid.substring(0, 6);
        }
    }

    @ManyToOne
    @JoinColumn(name = "clazz_id")
    private Clazz clazz;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<CourseSelection> courseSelections;
}