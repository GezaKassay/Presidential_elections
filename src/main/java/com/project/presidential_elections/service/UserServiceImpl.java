package com.project.presidential_elections.service;

import com.project.presidential_elections.entity.FirstRound;
import com.project.presidential_elections.entity.RoundEntity;
import com.project.presidential_elections.entity.SecondRound;
import com.project.presidential_elections.entity.UserEntity;
import com.project.presidential_elections.dto.UserDto;
import com.project.presidential_elections.repository.RoundRepository;
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
    private final RoundRepository roundRepository;

    public UserServiceImpl(HttpSession session, UserRepository userRepository, PasswordEncoder passwordEncoder, RoundRepository roundRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.session = session;
        this.roundRepository = roundRepository;
    }

    private RoundEntity useTable(String electionsName) throws IllegalArgumentException {
        return switch (electionsName) {
            case "FirstRound" -> new FirstRound();
            case "SecondRound" -> new SecondRound();
            default -> throw new IllegalArgumentException("Invalid election name: " + electionsName);
        };
    }

    @Override
    public void saveUser(UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
        }
        // encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);

        String electionsName = (String) session.getAttribute("electionsName");
        if (electionsName == null) {
            throw new IllegalStateException("Elections name is not set in session");
        }
        RoundEntity round = useTable(electionsName);
        round.setUser(user);
        roundRepository.save(round);
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

        String electionsName = (String) session.getAttribute("electionsName");
        RoundEntity round = user.getRounds().stream()
                .filter(r -> r.getClass().getSimpleName().equals(electionsName))
                .findFirst()
                .orElse(null);

        if (round != null) {
            userDto.setNumVotes(round.getNumVotes());
            userDto.setVoted(round.getNumVotes());
        }

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

        String electionsName = (String) session.getAttribute("electionsName");
        RoundEntity round = user.getRounds().stream()
                .filter(r -> r.getClass().getSimpleName().equals(electionsName))
                .findFirst()
                .orElse(null);

        if (round != null) {
            round.setVoted(Integer.valueOf("1"));
            roundRepository.save(round);
            userDto.setVoted(round.getVoted());
            System.out.println(userDto.getVoted());
        }

        UserEntity candidate = userRepository.getReferenceById(id);
        RoundEntity candidateRound = candidate.getRounds().stream()
                .filter(r -> r.getClass().getSimpleName().equals(electionsName))
                .findFirst()
                .orElse(null);

        if (candidateRound != null) {
           int votes = (candidateRound.getNumVotes() != null) ? candidateRound.getNumVotes() : 0;
            candidateRound.setNumVotes(votes + 1);
            roundRepository.save(candidateRound);
            userDto.setNumVotes(candidateRound.getNumVotes());
        }
    }
}
