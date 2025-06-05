package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepo extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByStudentId(Long studentId);
}
