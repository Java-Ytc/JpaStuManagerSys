package com.example.jpastumanagersys.controller;

import com.example.jpastumanagersys.entity.Clazz;
import com.example.jpastumanagersys.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clazz")
public class ClazzController {
    @Autowired
    private ClazzService clazzService;

    @GetMapping("/list")
    public String listClasses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        Page<Clazz> classPage = clazzService.getAllClasses(PageRequest.of(page, size));
        model.addAttribute("classes", classPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", classPage.getTotalPages());
        return "class-list";
    }

    @GetMapping("/add")
    public String addClassForm(Model model) {
        model.addAttribute("clazz", new Clazz());
        return "class-form";
    }

    @PostMapping("/save")
    public String saveClass(@ModelAttribute Clazz clazzEntity) {
        clazzService.saveClass(clazzEntity);
        return "redirect:/clazz/list";
    }

    @GetMapping("/edit/{id}")
    public String editClassForm(@PathVariable Long id, Model model) {
        Clazz clazzEntity = clazzService.getClassById(id);
        model.addAttribute("clazz", clazzEntity);
        return "class-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteClass(@PathVariable Long id) {
        clazzService.deleteClass(id);
        return "redirect:/clazz/list";
    }
}