package com.project.presidential_elections.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.project.presidential_elections.UserRepository;
import com.project.presidential_elections.UserEntity;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/presidential-elections/login-page")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/presidential-elections/login-user")
    public String loginUser(@ModelAttribute("user") UserEntity user, Model model) {
        user = userRepository.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (user == null) {
            model.addAttribute("msg", "Invalid Login and Password");
            return "login";
        } else {
            return "redirect:/presidential-elections/home";
        }
    }

}
