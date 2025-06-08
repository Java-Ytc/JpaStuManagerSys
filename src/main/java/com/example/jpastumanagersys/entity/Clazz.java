package com.example.jpastumanagersys.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "clazz")
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    @Column(unique = true)
    private String classCode;

    @PrePersist
    public void generateClassCode() {
        if (classCode == null) {
            // 生成一个唯一的6位数编号
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            classCode = uuid.substring(0, 6);
        }
    }

    // 一个班级可以有多个学生用户，一（班级）对多（学生用户）
    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL)
    private List<User> students;
}
