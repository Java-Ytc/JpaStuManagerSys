package com.example.jpastumanagersys.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;
    private String courseCode;
    private int maxStudents;
    private int currentStudents;
    private String courseTime;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseSelection> courseSelections;
}
