package com.example.jpastumanagersys.service;

import com.example.jpastumanagersys.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttendanceService {
    Attendance recordAttendance(Long studentId, Long courseId, boolean isPresent);

    List<Attendance> getAttendanceByStudent(Long studentId);

    List<Attendance> getAttendanceByCourse(Long courseId);

    Page<Attendance> getAttendanceByCourse(Long courseId, Pageable pageable);

    void deleteAttendance(Long id);
}
