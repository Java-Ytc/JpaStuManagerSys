package com.example.jpastumanagersys.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 一个学生可以有多个打分，每个分数对应一个学生，多对一
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    // 一个打分对应一个课程，一个课程可以有多个打分，多对一
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Double score;
}
