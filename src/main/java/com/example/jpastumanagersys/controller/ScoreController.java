package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.CourseSelection;
import com.example.jpastumanagersys.service.CourseSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/score")
public class ScoreController {
    @Autowired
    private CourseSelectionService selectionService;

    @GetMapping("/list/{courseId}")
    public String listScores(@PathVariable Long courseId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Page<CourseSelection> selectionPage = selectionService.getAllSelections(PageRequest.of(page, size));
        model.addAttribute("selections", selectionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", selectionPage.getTotalPages());
        model.addAttribute("courseId", courseId);
        return "score-list";
    }

    @PostMapping("/update/{selectionId}")
    public String updateScore(@PathVariable Long selectionId, @RequestParam Double score, @RequestParam Long courseId) {
        selectionService.updateScore(selectionId, score);
        return "redirect:/score/list/" + courseId;
    }
}
