package com.project.presidential_elections.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/presidential-elections/home")
    public String viewHomePage() {
        return "index";
    }
}