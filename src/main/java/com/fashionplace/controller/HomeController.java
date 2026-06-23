package com.fashionplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the public home page.
 */
@Controller
public class HomeController {

    /**
     * Shows the FashionPlace landing page.
     *
     * @return the {@code home} Thymeleaf view name
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
