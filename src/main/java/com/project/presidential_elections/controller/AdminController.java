package com.project.presidential_elections.controller;

import com.project.presidential_elections.entity.Elections;
import com.project.presidential_elections.repository.ElectionsRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;

@Controller
public class AdminController {

    private final ElectionsRepository electionsRepository;

    public AdminController(ElectionsRepository electionsRepository) {
        this.electionsRepository = electionsRepository;
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        List<Elections> elections = Arrays.asList(
                new Elections("1", "FirstRound"),
                new Elections("2", "SecondRound"),
                new Elections("3", "ThirdRound")
        );
        electionsRepository.saveAll(elections);
        model.addAttribute("elections", elections);
        return "adminForm";
    }

    @PostMapping("/form/submit")
    public String submitForm(@RequestParam("electionsName") String electionsName, HttpSession session) {
        session.setAttribute("electionsName", electionsName);
        return "redirect:/home";
    }
}
