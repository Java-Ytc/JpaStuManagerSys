package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.Clazz;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.repo.ClazzRepo;
import com.example.jpastumanagersys.repo.UserRepo;
import com.example.jpastumanagersys.service.ClazzService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {
    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private UserRepo userRepo;

    @Override
    public Clazz saveClass(Clazz classEntity) {
        return clazzRepo.save(classEntity);
    }

    @Override
    @Transactional
    public void deleteByClassCodes(List<String> classCodes) {
        for (String classCode : classCodes) {
            if (hasStudents(classCode)) {
                throw new IllegalStateException("教室 " + classCode + " 中已有学生，无法删除");
            }
        }
        clazzRepo.deleteAllByClassCodeIn(classCodes);
    }

    @Override
    public Clazz updateClass(Clazz clazz) {
        Clazz existingClass = clazzRepo.findByClassCode(clazz.getClassCode())
                .orElseThrow(() -> new IllegalArgumentException("更新班级信息时，没有找到对应编号的班级"));

        // 更新班级名称
        existingClass.setClassName(clazz.getClassName());

        // 处理班级下有学生的情况，级联修改学生的班级信息
        if (!existingClass.getStudents().isEmpty()) {
            List<User> students = existingClass.getStudents();
            for (User student : students) {
                student.setClazz(existingClass);
                userRepo.save(student);
            }
        }

        return clazzRepo.save(existingClass);
    }

    @Override
    public Clazz getByClassCode(String classCode) {
        return clazzRepo.findByClassCode(classCode).orElseThrow(() -> new IllegalArgumentException("获取班级信息时，没有找到对应编号的班级"));
    }

    @Override
    public Page<Clazz> getAllClasses(Pageable pageable) {
        List<Clazz> allClazzes = clazzRepo.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allClazzes.size());

        List<Clazz> clazzPageContent = allClazzes.subList(start, end);
        // 为每个班级设置学生信息
        for (Clazz clazz : clazzPageContent) {
            List<User> students = userRepo.findByClazz_ClassCode(clazz.getClassCode());
            clazz.setStudents(students);
        }

        return new PageImpl<>(clazzPageContent, pageable, allClazzes.size());
    }

    @Override
    public boolean hasStudents(String classCode) {
        Clazz clazz = clazzRepo.findByClassCode(classCode).orElseThrow(() -> new IllegalArgumentException("检查班级是否有学生时，没有找到对应编号的班级"));
        return !clazz.getStudents().isEmpty();
    }

    @Override
    public Page<Clazz> getByClassNameContaining(String className, Pageable pageable) {
        List<Clazz> allClasses = clazzRepo.findByClassNameContaining(className);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allClasses.size());

        List<Clazz> clazzPageContent = allClasses.subList(start, end);
        // 为每个班级设置学生信息
        for (Clazz clazz : clazzPageContent) {
            List<User> students = userRepo.findByClazz_ClassCode(clazz.getClassCode());
            clazz.setStudents(students);
        }

        return new PageImpl<>(clazzPageContent, pageable, allClasses.size());
    }
}
