package com.example.jpastumanagersys.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;

    @Column(unique = true)
    private String courseCode;

    @PrePersist
    public void generateCourseCode() {
        if (courseCode == null) {
            // 生成一个唯一的6位数编号
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            courseCode = uuid.substring(0, 6);
        }
    }

    private int maxStudents;
    private int currentStudents;
    private String courseTime;

    // 一个 course（课程）只能由一个 user（教师）授课，一个 user（教师）可以教授多个 course（课程），多（course）对一（user教师）
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    // 一个 course（课程）可以有多个 course_selection（选课记录），一个 course_selection（选课记录）只能有以个 course（课程），一（course）对多（course_selection）
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseSelection> courseSelections;
}
