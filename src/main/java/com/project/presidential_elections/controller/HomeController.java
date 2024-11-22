package com.project.presidential_elections.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/index")
    public String viewHomePage() {
        return "index";
    }
}
