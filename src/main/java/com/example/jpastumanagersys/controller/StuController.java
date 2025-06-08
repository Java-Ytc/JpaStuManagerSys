package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.service.CourseSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stu")
public class StuController {
    @Autowired
    private CourseSelectionService selectionService;

}
