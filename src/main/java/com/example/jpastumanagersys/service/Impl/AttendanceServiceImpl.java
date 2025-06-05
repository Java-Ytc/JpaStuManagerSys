package com.example.jpastumanagersys.service.Impl;

import com.example.jpastumanagersys.entity.Attendance;
import com.example.jpastumanagersys.entity.Course;
import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.repo.AttendanceRepo;
import com.example.jpastumanagersys.repo.CourseRepo;
import com.example.jpastumanagersys.repo.UserRepo;
import com.example.jpastumanagersys.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceRepo attendanceRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private CourseRepo courseRepository;

    @Override
    public Attendance recordAttendance(Long studentId, Long courseId, boolean isPresent) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setCourse(course);
        attendance.setAttendanceDate(new Date());
        attendance.setPresent(isPresent);

        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getAttendanceByStudent(Long studentId) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        return attendanceRepository.findByStudent(student);
    }

    @Override
    public List<Attendance> getAttendanceByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        return attendanceRepository.findByCourse(course);
    }

    @Override
    public Page<Attendance> getAttendanceByCourse(Long courseId, Pageable pageable) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        return attendanceRepository.findByCourse(course, pageable);
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}