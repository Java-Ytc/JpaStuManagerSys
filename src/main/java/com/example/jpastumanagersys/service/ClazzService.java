package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.Clazz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClazzService {
    // 保存新班级
    Clazz saveClass(Clazz clazz);

    // 根据班级编号删除班级
    void deleteByClassCode(String classCode);

    // 更新班级信息
    Clazz updateClass(Clazz classEntity);

    // 根据班级编号获取班级信息
    Clazz getByClassCode(String classCode);

    // 获取所有班级信息
    Page<Clazz> getAllClasses(Pageable pageable);

    // 判断班级是否有学生
    boolean hasStudents(String classCode);

    // 根据班级名称查找班级信息
    Page<Clazz> getClassesByClassName(String className, Pageable pageable);
}
