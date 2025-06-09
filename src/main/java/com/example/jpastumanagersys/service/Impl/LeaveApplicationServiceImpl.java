package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.LeaveApplication;
import com.example.jpastumanagersys.entity.LeaveStatus;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.repo.LeaveApplicationRepo;
import com.example.jpastumanagersys.repo.UserRepo;
import com.example.jpastumanagersys.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveApplicationServiceImpl implements LeaveApplicationService {
    @Autowired
    private LeaveApplicationRepo leaveApplicationRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public LeaveApplication applyForLeave(Long studentId, LocalDate startDate, LocalDate endDate, String reason) {
        User student = userRepo.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setStudent(student);
        leaveApplication.setStartDate(startDate);
        leaveApplication.setEndDate(endDate);
        leaveApplication.setReason(reason);
        // 默认状态为待审核
        leaveApplication.setStatus(LeaveStatus.PENDING);

        return leaveApplicationRepo.save(leaveApplication);
    }

    @Override
    public List<LeaveApplication> getLeaveApplicationsByStudent(Long studentId) {
        User student = userRepo.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return leaveApplicationRepo.findByStudent(student);
    }

    @Override
    public Page<LeaveApplication> getLeaveApplicationsByStudent(Long studentId, Pageable pageable) {
        User student = userRepo.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return leaveApplicationRepo.findByStudent(student, pageable);
    }
}
