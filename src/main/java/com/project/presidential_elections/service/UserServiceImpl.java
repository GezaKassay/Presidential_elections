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
import java.util.Optional;
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
        System.out.println("Received electionsName: " + electionsName);
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
        RoundEntity round = useTable(electionsName);

        Optional<RoundEntity> userRound = user.getRounds().stream()
                .filter(r -> r.getId() == round.getId()) // Match round ID
                .findFirst();

        userDto.setNumVotes(userRound.map(RoundEntity::getNumVotes).orElse(0));
        userDto.setVoted(userRound.map(RoundEntity::getVoted).orElse(0));

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
        RoundEntity round = useTable(electionsName);

        Optional<RoundEntity> userRound = user.getRounds().stream()
                .filter(r -> r.getId() == round.getId())
                .findFirst();

        if (userRound.isPresent() && userRound.get().getVoted() != null && userRound.get().getVoted() == 1) {
            throw new IllegalStateException("User has already voted in this round");
        }

        round.setVoted(Integer.valueOf("1"));
        roundRepository.save(round);

        UserEntity candidate = userRepository.getReferenceById(id);

        Optional<RoundEntity> candidateRound = candidate.getRounds().stream()
                .filter(r -> r.getId() == round.getId())
                .findFirst();

        if (candidateRound.isPresent()) {
            RoundEntity candidateVoteRound = candidateRound.get();
            int votes = candidateVoteRound.getNumVotes() != null ? candidateVoteRound.getNumVotes() : 0;
            candidateVoteRound.setNumVotes(votes + 1);
            roundRepository.save(candidateVoteRound);
        } else {
            throw new IllegalStateException("Candidate is not part of this round");
        }
    }
}
