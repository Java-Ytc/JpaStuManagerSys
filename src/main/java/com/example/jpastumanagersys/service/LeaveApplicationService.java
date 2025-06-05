package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.LeaveApplication;

import java.util.List;

public interface LeaveApplicationService {
    LeaveApplication saveLeaveApplication(LeaveApplication leaveApplication);
    LeaveApplication updateLeaveApplication(LeaveApplication leaveApplication);
    void deleteLeaveApplication(Long id);
    LeaveApplication getLeaveApplicationById(Long id);
    List<LeaveApplication> getLeaveApplicationsByStudent(Long studentId);
}
