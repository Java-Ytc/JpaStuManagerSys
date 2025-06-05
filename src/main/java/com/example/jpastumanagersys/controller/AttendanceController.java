package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.Attendance;
import com.example.jpastumanagersys.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/list/{courseId}")
    public String listAttendance(@PathVariable Long courseId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Page<Attendance> attendancePage = attendanceService.getAttendanceByCourse(courseId, PageRequest.of(page, size));
        model.addAttribute("attendances", attendancePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", attendancePage.getTotalPages());
        model.addAttribute("courseId", courseId);
        return "attendance-list";
    }

    @GetMapping("/record/{courseId}/{studentId}")
    public String recordAttendance(@PathVariable Long courseId, @PathVariable Long studentId, @RequestParam boolean isPresent) {
        attendanceService.recordAttendance(studentId, courseId, isPresent);
        return "redirect:/attendance/list/" + courseId;
    }

    @GetMapping("/delete/{id}/{courseId}")
    public String deleteAttendance(@PathVariable Long id, @PathVariable Long courseId) {
        attendanceService.deleteAttendance(id);
        return "redirect:/attendance/list/" + courseId;
    }
}
