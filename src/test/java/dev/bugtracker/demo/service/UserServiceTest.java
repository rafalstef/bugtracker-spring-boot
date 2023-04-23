package dev.bugtracker.demo.service;

import dev.bugtracker.demo.dto.UserSimpleDto;
import dev.bugtracker.demo.enumeration.UserRole;
import dev.bugtracker.demo.exception.ResourceNotFoundException;
import dev.bugtracker.demo.mapper.UserMapper;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserService sut;


    @Test
    void loadUserByUsername_ValidUser_ReturnUserDetails() {
        String email = "john_doe";
        User user = User.builder()
                .email(email)
                .password("password")
                .role(UserRole.DEVELOPER)
                .isEnabled(Boolean.FALSE)
                .build();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        UserDetails userDetails = sut.loadUserByUsername(email);

        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(user.getRole().getGrantedAuthorities(), userDetails.getAuthorities());
        assertEquals(user.getIsEnabled(), userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_NonExistingUser_ThrowsUsernameNotFoundException() {
        String email = "john_doe";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> sut.loadUserByUsername(email));
    }

    @Test
    void list_ExistingUser_ReturnsListOfUserGetDto() {
        User user1 = User.builder()
                .email("jim@email.com")
                .password("pass123")
                .build();
        User user2 = User.builder()
                .email("andy@email.com")
                .password("pass321")
                .build();
        List<User> list = Arrays.asList(user1, user2);

        when(userRepo.findAll()).thenReturn(list);
        sut.list();

        verify(userRepo).findAll();
        verify(mapper).usersToUserGetDtoList(list);
    }

    @Test
    void list_NonUsers_ReturnsEmptyList() {
        List<User> emptyList = Collections.emptyList();

        when(userRepo.findAll()).thenReturn(emptyList);
        sut.list();

        verify(userRepo).findAll();
        verify(mapper).usersToUserGetDtoList(emptyList);

    }

    @Test
    void get_UserExists_ReturnUserGetDto() {
        Long id = 65L;
        User user = User.builder()
                .id(id)
                .email("michael@email.com")
                .build();

        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        sut.getById(id);

        verify(userRepo).findById(id);
        verify(mapper).userToUserGetDto(user);
    }

    @Test
    void get_NonExistingUser_ThrowsResourceNotFoundException() {
        Long id = 65L;

        when(userRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sut.getById(id));

        verify(userRepo).findById(id);
    }

    @Test
    void updateName_UserExists_UpdatesFirstAndLastName() {
        Long id = 20L;
        String newFirstName = "Michael";
        String newLastName = "Scott";

        UserSimpleDto simpleDto = new UserSimpleDto(id, newFirstName + " " + newLastName);

        User user = User.builder()
                .firstName("michale")
                .lastName("scott")
                .build();

        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        sut.updateName(id, simpleDto);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepo).save(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();

        verify(mapper).userToUserGetDto(updatedUser);

        assertEquals(newFirstName, updatedUser.getFirstName());
        assertEquals(newLastName, updatedUser.getLastName());
    }

    @Test
    void getUsersAssignedToProjectWithId_ValidProjectId_ReturnsListOfUserGetDto() {
        Long projectId = 2L;
        Set<User> userSet = Collections.singleton(User.builder().id(10L).email("john@email.com").build());

        when(userRepo.findUsersAssignedToProjectById(projectId)).thenReturn(userSet);
        sut.getUsersAssignedToProjectWithId(projectId);

        verify(userRepo).findUsersAssignedToProjectById(projectId);
        verify(mapper).toSimpleDtoSet(userSet);
    }

    @Test
    void getUsersAssignedToProjectWithId_InvalidProjectId_ReturnsEmptyList() {
        Long projectId = 2L;
        Set<User> userSet = Collections.emptySet();

        when(userRepo.findUsersAssignedToProjectById(projectId)).thenReturn(userSet);
        sut.getUsersAssignedToProjectWithId(projectId);

        verify(userRepo).findUsersAssignedToProjectById(projectId);
        verify(mapper).toSimpleDtoSet(userSet);
    }

    @Test
    void getUsersAssignedToTicketWithId_TicketExists_ReturnsListOfUserGetDto() {
        Long ticketId = 2L;
        Set<User> userSet = Collections.singleton(User.builder().id(10L).email("john@email.com").build());

        when(userRepo.findByAssignedTicket(ticketId)).thenReturn(userSet);
        sut.getUsersAssignedToTicketWithId(ticketId);

        verify(userRepo).findByAssignedTicket(ticketId);
        verify(mapper).toSimpleDtoSet(userSet);
    }

    @Test
    void getUsersAssignedToTicketWithId_TicketDoesNotExists_ReturnsEmptyList() {
        Long ticketId = 2L;
        Set<User> userSet = Collections.emptySet();

        when(userRepo.findByAssignedTicket(ticketId)).thenReturn(userSet);
        sut.getUsersAssignedToTicketWithId(ticketId);

        verify(userRepo).findByAssignedTicket(ticketId);
        verify(mapper).toSimpleDtoSet(userSet);
    }
}