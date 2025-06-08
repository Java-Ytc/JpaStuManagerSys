package com.example.jpastumanagersys.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "leave_applications")
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 一个学生可以有多条请假申请，一个请假记录只能对应一个学生用户，多（请假记录）对一（学生用户）
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private Date startDate;
    private Date endDate;
    private String reason;
    private boolean approved;
}
