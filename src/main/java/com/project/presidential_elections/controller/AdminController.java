package com.project.presidential_elections.controller;

import com.project.presidential_elections.dto.UserDto;
import com.project.presidential_elections.entity.DynamicRound;
import com.project.presidential_elections.entity.Elections;
import com.project.presidential_elections.entity.UserEntity;
import com.project.presidential_elections.repository.ElectionsRepository;
import com.project.presidential_elections.repository.RoundRepository;
import com.project.presidential_elections.repository.UserRepository;
import com.project.presidential_elections.service.UserService;
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
    private UserService userService;
    private RoundRepository roundRepository;
    private UserRepository userRepository;

    public AdminController(ElectionsRepository electionsRepository, UserService userService, RoundRepository roundRepository, UserRepository userRepository) {
        this.electionsRepository = electionsRepository;
        this.userService = userService;
        this.roundRepository = roundRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/new-round")
    public String newRoundForm(Model model) {
        UserDto userDto = userService.getCurrentUser();
        model.addAttribute("user", userDto);
        DynamicRound round = new DynamicRound();
        model.addAttribute("round", round);
        return "roundForm";
    }

    @PostMapping("/new-round/create")
    public String createRound(@RequestParam String type, @RequestParam Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        DynamicRound roundEntity = new DynamicRound(type, 0, 0, 0, user);
        roundRepository.save(roundEntity);
        return "redirect:/user/home-page";
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
    public String submitForm(@RequestParam String electionsName, HttpSession session) {
        session.setAttribute("electionsName", electionsName);
        userService.associateUsersWithNewRound();
        return "redirect:/home";
    }
}
