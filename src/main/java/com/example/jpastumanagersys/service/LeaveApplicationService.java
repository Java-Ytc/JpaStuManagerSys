package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.LeaveApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LeaveApplicationService {
    LeaveApplication applyForLeave(Long studentId, LocalDate startDate, LocalDate endDate, String reason);
    List<LeaveApplication> getLeaveApplicationsByStudent(Long studentId);
    Page<LeaveApplication> getLeaveApplicationsByStudent(Long studentId, Pageable pageable);
}
