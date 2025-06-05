package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.Clazz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClazzService {
    Clazz saveClass(Clazz classEntity);
    void deleteClass(Long id);
    Clazz updateClass(Clazz classEntity);
    Clazz getClassById(Long id);
    List<Clazz> getAllClasses();
    Page<Clazz> getAllClasses(Pageable pageable);
    boolean hasStudents(Long classId);
}
