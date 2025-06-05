package com.example.jpastumanagersys.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "clazz_id")
    private Clazz studentClazz;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CourseSelection> courseSelections;
}