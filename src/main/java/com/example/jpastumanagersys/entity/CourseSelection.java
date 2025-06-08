package com.example.jpastumanagersys.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "course_selection")
public class CourseSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 一个学生用户可以有多条选课记录，多（选课记录）对一（学生用户）
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    // 一个课程可以对应多条选课记录，多（选课记录）对一（课程）
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Date selectionDate;

    private Double score;
}
