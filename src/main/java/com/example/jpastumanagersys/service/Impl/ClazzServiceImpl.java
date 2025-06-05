package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.Clazz;
import com.example.jpastumanagersys.repo.ClazzRepo;
import com.example.jpastumanagersys.repo.UserRepo;
import com.example.jpastumanagersys.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {
    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private UserRepo userRepository;

    @Override
    public Clazz saveClass(Clazz classEntity) {
        return clazzRepo.save(classEntity);
    }

    @Override
    public void deleteClass(Long id) {
        if (!hasStudents(id)) {
            clazzRepo.deleteById(id);
        } else {
            throw new IllegalStateException("Cannot delete a class with students.");
        }
    }

    @Override
    public Clazz updateClass(Clazz classEntity) {
        return clazzRepo.save(classEntity);
    }

    @Override
    public Clazz getClassById(Long id) {
        return clazzRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Class not found"));
    }

    @Override
    public List<Clazz> getAllClasses() {
        return clazzRepo.findAll();
    }

    @Override
    public Page<Clazz> getAllClasses(Pageable pageable) {
        return clazzRepo.findAll(pageable);
    }

    @Override
    public boolean hasStudents(Long classId) {
        Clazz classEntity = clazzRepo.findById(classId).orElseThrow(() -> new IllegalArgumentException("Class not found"));
        return !classEntity.getStudents().isEmpty();
    }
}
