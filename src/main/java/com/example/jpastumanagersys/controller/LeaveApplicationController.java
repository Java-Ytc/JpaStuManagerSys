package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.LeaveApplication;
import com.example.jpastumanagersys.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/leave")
public class LeaveApplicationController {
    @Autowired
    private LeaveApplicationService leaveApplicationService;

    @GetMapping("/apply")
    public String applyLeaveForm(Model model) {
        model.addAttribute("leaveApplication", new LeaveApplication());
        return "leave-apply";
    }

    @PostMapping("/save")
    public String saveLeaveApplication(@ModelAttribute LeaveApplication leaveApplication) {
        leaveApplicationService.saveLeaveApplication(leaveApplication);
        return "redirect:/leave/list";
    }

    @GetMapping("/list")
    public String listLeaveApplications(@RequestParam Long studentId, Model model) {
        List<LeaveApplication> leaveApplications = leaveApplicationService.getLeaveApplicationsByStudent(studentId);
        model.addAttribute("leaveApplications", leaveApplications);
        return "leave-list";
    }
}
