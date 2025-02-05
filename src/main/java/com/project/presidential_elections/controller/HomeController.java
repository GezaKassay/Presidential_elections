package com.project.presidential_elections.controller;

import com.project.presidential_elections.dto.UserDto;
import com.project.presidential_elections.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {

    private UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String userStandings(Model model, HttpSession session){
        List<UserDto> users = userService.findAllUsers();
        List<UserDto> sortedUsers = users.stream()
                                         .sorted(Comparator.comparing(UserDto -> UserDto.getNumVotes() == null ? 0 : UserDto.getNumVotes(), Comparator.reverseOrder()))
                                         .toList();
        model.addAttribute("users", sortedUsers);
        String electionsName = (String) session.getAttribute("electionsName");
        model.addAttribute("electionsName", electionsName);
        return "index";
    }
}
