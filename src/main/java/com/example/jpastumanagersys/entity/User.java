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

    // 一个user（学生）只能关联一个 clazz，一个 clazz 可以有多个学生，多（学生）对一（教室）
    @ManyToOne
    @JoinColumn(name = "clazz_id")
    private Clazz clazz;

    // 一个user（学生）可以有多个 course_selection，一个 course_selection 对应一个user（学生），一（user 学生）对多（course_selection 选课记录）
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<CourseSelection> courseSelections;

    // 一个user（教师）可以有多个 course，一个 course 只能对应一个user（教师），多（user 教师）对一（course 课程）
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Course> courses;
}