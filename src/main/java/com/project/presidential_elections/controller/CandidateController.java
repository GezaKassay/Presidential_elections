package com.project.presidential_elections.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import com.project.presidential_elections.dto.UserDto;
import com.project.presidential_elections.service.UserService;

@Controller
public class CandidateController {

    private UserService userService;

    public CandidateController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/candidate/apply")
    public String updateRole(@ModelAttribute("user") UserDto userDto) {
        userService.updateRole(userDto);
        return "redirect:/user/home-page";
    }

    @GetMapping("/candidate/profile/{id}")
    public String showCandidateProfile(@PathVariable(value = "id") long id, Model model) {
        UserDto candidate = userService.getById(id);
        UserDto currentUser = userService.getCurrentUser();
        model.addAttribute("candidate", candidate);
        model.addAttribute("currentUser", currentUser);
        return "candidateProfile";
    }

    @PostMapping("/candidate/profile/{id}/vote")
    public String updateVote(@PathVariable(value = "id") long id, UserDto userDto) {
        userService.updateVote(userDto, id);
        return "redirect:/user/home-page";
    }
}
