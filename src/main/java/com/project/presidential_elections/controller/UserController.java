package com.project.presidential_elections.controller;

import com.project.presidential_elections.entity.UserEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import com.project.presidential_elections.dto.UserDto;
import com.project.presidential_elections.service.UserService;

import java.util.List;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/login")
    public String login(){
        return "login";
    }

    @GetMapping("/user/register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/user/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model){
        UserEntity existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", "101",
                "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }

        userService.saveUser(userDto);
        return "redirect:/user/register?success";
    }

    @GetMapping("/user/home-page")
    public String users(Model model, HttpSession session){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        String electionsName = (String) session.getAttribute("electionsName");
        model.addAttribute("electionsName", electionsName);
        UserDto userDto = userService.getCurrentUser();
        model.addAttribute("currentUser", userDto);
        return "userLoginPage";
    }

    @GetMapping("/user/details")
    public String viewUserAccountForm(Model model) {
        UserDto userDto = userService.getCurrentUser();
        model.addAttribute("user", userDto);
        return "userAccount";
    }

    @GetMapping("/user/description/page")
    public String editUserDescription(Model model) {
        UserDto userDto = userService.getCurrentUser();
        model.addAttribute("user", userDto);
        return "description";
    }

    @PostMapping("/user/description/save")
    public String saveDescription(@ModelAttribute("user") UserDto userDto) {
        userService.saveDescription(userDto);
        return "redirect:/user/details";
    }
}
