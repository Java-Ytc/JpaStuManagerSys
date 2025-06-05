package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.LeaveApplication;
import com.example.jpastumanagersys.repo.LeaveApplicationRepo;
import com.example.jpastumanagersys.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveApplicationServiceImpl implements LeaveApplicationService {
    @Autowired
    private LeaveApplicationRepo leaveApplicationRepository;

    @Override
    public LeaveApplication saveLeaveApplication(LeaveApplication leaveApplication) {
        return leaveApplicationRepository.save(leaveApplication);
    }

    @Override
    public LeaveApplication updateLeaveApplication(LeaveApplication leaveApplication) {
        return leaveApplicationRepository.save(leaveApplication);
    }

    @Override
    public void deleteLeaveApplication(Long id) {
        leaveApplicationRepository.deleteById(id);
    }

    @Override
    public LeaveApplication getLeaveApplicationById(Long id) {
        return leaveApplicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Leave application not found"));
    }

    @Override
    public List<LeaveApplication> getLeaveApplicationsByStudent(Long studentId) {
        return leaveApplicationRepository.findByStudentId(studentId);
    }
}
