package com.example.jpastumanagersys.repo;

import com.example.jpastumanagersys.entity.LeaveApplication;
import com.example.jpastumanagersys.entity.LeaveStatus;
import com.example.jpastumanagersys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepo extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByStudent(User student);

    List<LeaveApplication> findByStatus(LeaveStatus status);
}
