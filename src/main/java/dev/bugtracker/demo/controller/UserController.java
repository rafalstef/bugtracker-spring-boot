package dev.bugtracker.demo.controller;

import dev.bugtracker.demo.dto.UserGetDto;
import dev.bugtracker.demo.dto.UserSimpleDto;
import dev.bugtracker.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<List<UserGetDto>> getUserList() {
        return ResponseEntity
                .status(OK)
                .body(userService.list());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<Set<UserSimpleDto>> getUsersAssignedToTicket(@PathVariable(value = "ticketId") @Min(1) Long ticketId) {
        return ResponseEntity
                .status(OK)
                .body(userService.getUsersAssignedToTicketWithId(ticketId));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Set<UserSimpleDto>> getUsersAssignedToProject(@PathVariable(value = "projectId")
                                                                        @Min(1) Long projectId) {
        return ResponseEntity
                .status(OK)
                .body(userService.getUsersAssignedToProjectWithId(projectId));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserGetDto> getUser(@PathVariable(value = "userId") @Min(1) Long id) {
        return ResponseEntity
                .status(OK)
                .body(userService.getById(id));
    }


    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/{userId}")
    public ResponseEntity<UserGetDto> updateUsersName(@PathVariable(value = "userId") @Min(1) Long id,
                                                      @RequestBody @Valid UserSimpleDto userSimpleDto) {
        return ResponseEntity
                .status(OK)
                .body(userService.updateName(id, userSimpleDto));
    }

}
