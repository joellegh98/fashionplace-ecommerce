package com.fashionplace.controller;

import com.fashionplace.service.RecommendationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final RecommendationService recommendationService;

    public HomeController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recommendations", recommendationService.recommendForCurrentUser());
        return "home";
    }
}
