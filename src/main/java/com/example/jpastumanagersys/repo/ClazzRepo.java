package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClazzRepo extends JpaRepository<Clazz, Long> {
    // 根据班级编号查找
    Optional<Clazz> findByClassCode(String classCode);

    // 根据班级编号删除
    void deleteAllByClassCodeIn(List<String> classCodes);

    // 根据班级名称模糊查找
    List<Clazz> findByClassNameContaining(String className);
}
