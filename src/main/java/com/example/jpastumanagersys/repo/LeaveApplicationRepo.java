package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.LeaveApplication;
import com.example.jpastumanagersys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepo extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByStudent(User student);
    Page<LeaveApplication> findByStudent(User student, Pageable pageable);
}
