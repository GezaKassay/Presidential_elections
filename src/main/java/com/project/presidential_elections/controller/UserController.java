package com.project.presidential_elections.controller;

import com.project.presidential_elections.entity.UserEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/user/show-candidates")
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "candidates";
    }

    @GetMapping("/user/account")
    public String viewUserAccountForm(Model model) {
        UserDto userDto = userService.getCurrentUser();
        model.addAttribute("user", userDto);
        return "userAccount";
    }

    @GetMapping("/user/description")
    public String editUserDescription(Model model) {
        UserDto userDto = userService.getCurrentUser();
        model.addAttribute("user", userDto);
        return "description";
    }

    @PostMapping("/user/description/save")
    public String saveDescription(@ModelAttribute("user") UserDto userDto) {
        userService.saveDescription(userDto);
        return "redirect:/user/account";
    }

    @PostMapping("/user/apply")
    public String updateRole(@ModelAttribute("user") UserDto userDto) {
        userService.updateRole(userDto);
        return "redirect:/user/show-candidates";
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
        return "redirect:/user/show-candidates";
    }
}
