package com.project.presidential_elections.service;

import com.project.presidential_elections.entity.UserEntity;
import com.project.presidential_elections.dto.UserDto;
import com.project.presidential_elections.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        if (user.getRole() == null) {
            user.setRole("ROLE_ADMIN");
        }
        // encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getCurrentUser() {
        // Get the currently authenticated user's username (or principal)
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Retrieve the user entity from the repository using the username
        UserEntity userEntity = userRepository.findByEmail(currentUsername);

        // Map the UserEntity to UserDto
        return mapToUserDto(userEntity);
    }

    private UserDto mapToUserDto(UserEntity user){
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setShortDescription(user.getShortDescription());
        userDto.setRole(user.getRole());
        return userDto;
    }

}
