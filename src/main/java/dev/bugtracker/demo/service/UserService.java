package dev.bugtracker.demo.service;

import dev.bugtracker.demo.exception.ResourceNotFoundException;
import dev.bugtracker.demo.dto.UserGetDto;
import dev.bugtracker.demo.dto.UserSimpleDto;
import dev.bugtracker.demo.mapper.UserMapper;
import dev.bugtracker.demo.model.SecurityUser;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final UserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database: " + username));
        log.info("User found in the database: {}", username);
        return new SecurityUser(user);
    }

    public List<UserGetDto> list() {
        log.info("Fetching all users");
        return mapper.usersToUserGetDtoList(userRepo.findAll());
    }

    public UserGetDto getById(Long id) {
        log.info("Fetching user by id {}", id);
        return mapper.userToUserGetDto(userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found."))
        );
    }

    public UserGetDto updateName(Long id, UserSimpleDto userSimpleDto) {
        User userToUpdate = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found."));

        log.info("Updating user with id {}", id);

        String fullName = userSimpleDto.getName();
        int lastSpaceIdx = fullName.lastIndexOf(" ");

        String firstName = fullName.substring(0,lastSpaceIdx);
        String lastName = fullName.substring(lastSpaceIdx+1);

        userToUpdate.setFirstName(firstName);
        userToUpdate.setLastName(lastName);

        userRepo.save(userToUpdate);

        return mapper.userToUserGetDto(userToUpdate);
    }

    public Set<UserSimpleDto> getUsersAssignedToProjectWithId(Long projectId) {
        log.info("Fetching users from project with id {}", projectId);
        return mapper.toSimpleDtoSet(userRepo.findUsersAssignedToProjectById(projectId));
    }

    public Set<UserSimpleDto> getUsersAssignedToTicketWithId(Long ticketId) {
        log.info("Fetching users assigned to ticket with id {}", ticketId);
        return mapper.toSimpleDtoSet(userRepo.findByAssignedTicket(ticketId));
    }
}
