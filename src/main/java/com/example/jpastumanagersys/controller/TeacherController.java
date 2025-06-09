package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.service.CourseSelectionService;
import com.example.jpastumanagersys.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSelectionService selectionService;



}
