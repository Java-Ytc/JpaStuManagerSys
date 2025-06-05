package com.example.jpastumanagersys.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "Clazz")
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;
    private String classCode;

    @OneToMany(mappedBy = "studentClazz", cascade = CascadeType.ALL)
    private List<User> students;
}
