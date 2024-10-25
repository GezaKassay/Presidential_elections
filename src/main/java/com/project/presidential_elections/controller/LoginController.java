package com.project.presidential_elections.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/presidential-elections/login")
    public String login(){
        return "login";
    }

}
