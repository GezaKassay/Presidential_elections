package com.project.presidential_elections.service;

import com.project.presidential_elections.entity.UserEntity;
import com.project.presidential_elections.dto.UserDto;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    UserEntity findUserByEmail(String email);

    List<UserDto> findAllUsers();

    UserDto getCurrentUser();

    void saveDescription(UserDto userDto);

    void updateRole(UserDto userDto);
}
