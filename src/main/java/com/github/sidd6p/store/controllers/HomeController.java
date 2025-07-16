package com.github.sidd6p.store.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @Value("${spring.application.name}")
    private  String appName;

    @RequestMapping("/")
    // The Model object is used to pass data from the controller to the view (Thymeleaf template).
    public String Index(Model model) {
        model.addAttribute("message", "Hello, Earth!");

        System.out.println("Application Name: " + appName);
        // Spring Boot looks for index.html in
        // - src/main/resources/static/
        // - src/main/resources/public/
        // - src/main/resources/META-INF/resources/
        // - src/main/resources/templates/ (if using Thymeleaf)
        return "index";

    }

}
