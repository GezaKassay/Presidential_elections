package com.project.presidential_elections.service;

import com.project.presidential_elections.entity.FirstRound;
import com.project.presidential_elections.entity.SecondRound;
import com.project.presidential_elections.entity.UserEntity;
import com.project.presidential_elections.dto.UserDto;
import com.project.presidential_elections.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final HttpSession session;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(HttpSession session, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.session = session;
    }

    private UserEntity useTable(String electionsName) {
        switch (electionsName) {
            case "FirstRound":
                return new FirstRound();
            case "SecondRound":
                return new SecondRound();
           // case "ThirdRound":
           //     return new ThirdRound();
            default:
                throw new IllegalArgumentException("Invalid election name: " + electionsName);
        }
    }

    @Override
    public void saveUser(UserDto userDto) {
        String electionsName = (String) session.getAttribute("electionsName");
        UserEntity user = useTable(electionsName);
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
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
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUsername);
        return mapToUserDto(userEntity);
    }

    @Override
    public UserDto getById(long id) {
        UserEntity user = userRepository.getReferenceById(id);
        return mapToUserDto(user);
    }

    private UserDto mapToUserDto(UserEntity user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setShortDescription(user.getShortDescription());
        userDto.setRole(user.getRole());
        userDto.setNumVotes(user.getNumVotes());
        userDto.setVoted(user.getVoted());
        return userDto;
    }

    @Override
    public void saveDescription(UserDto userDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(currentUsername);
        user.setShortDescription(userDto.getShortDescription());
        userRepository.save(user);
    }

    @Override
    public void updateRole(UserDto userDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(currentUsername);
        user.setRole("ROLE_CANDIDATE");
        userRepository.save(user);
    }

    @Override
    public void updateVote(UserDto userDto, long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(currentUsername);
        user.setVoted(Integer.valueOf("1"));
        userRepository.save(user);
        UserEntity candidate = userRepository.getReferenceById(id);
        if (candidate.getNumVotes() == null) {
            candidate.setNumVotes(0);
        }
        int votes = candidate.getNumVotes();
        candidate.setNumVotes(votes + 1);
        userRepository.save(candidate);
    }
}
