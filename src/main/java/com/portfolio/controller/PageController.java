package com.portfolio.controller;

import com.portfolio.model.Project;
import com.portfolio.repository.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Serves the monolithic index.html Thymeleaf template.
 * Injects model attributes for server-side rendered project cards.
 */
@Controller
public class PageController {

    private final ProjectRepository projectRepository;

    public PageController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Project> projects = projectRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("projects", projects);
        model.addAttribute("developerName", "Rohan Bisht");
        model.addAttribute("developerHandle", "@sys.rb");
        model.addAttribute("appVersion", "2.0.0");
        return "index";
    }

    @GetMapping("/resume")
    public String downloadResume() {
        return "redirect:/resume.pdf";
    }
}
