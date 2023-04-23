package dev.bugtracker.demo.controller;

import dev.bugtracker.demo.dto.StatusDto;
import dev.bugtracker.demo.dto.TicketDto;
import dev.bugtracker.demo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PreAuthorize("@customSecurity.isAuthorOrAssignedToTicket(#id) || hasAnyAuthority('SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable("id") @Min(1) Long id) {
        return ResponseEntity
                .status(OK)
                .body(ticketService.get(id));
    }


    @PreAuthorize("hasAnyAuthority('SCOPE_TESTER', 'SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @PostMapping()
    public ResponseEntity<TicketDto> saveTicket(@RequestBody @Valid TicketDto ticketDto) {
        return ResponseEntity
                .status(CREATED)
                .body(ticketService.create(ticketDto));
    }


    @PreAuthorize("@customSecurity.isAuthorOfTicket(#id) || hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable("id") @Min(1) Long id,
                                                  @RequestBody @Valid TicketDto ticketDto) {
        return ResponseEntity
                .status(OK)
                .body(ticketService.update(id, ticketDto));
    }


    @PreAuthorize("@customSecurity.isAuthorOfTicket(#id) || hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteTicket(@PathVariable("id") @Min(1) Long id) {
        Boolean idDeleted = ticketService.delete(id);
        if (!idDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id);
        }
        return ResponseEntity.status(OK).body(id);
    }


    @PreAuthorize("@customSecurity.isAuthorized(#userId) || hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    @GetMapping("/users/{userId}") // get all ticket assigned to specific user
    public ResponseEntity<List<TicketDto>> getTicketsAssignedToUser(@PathVariable("userId") @Min(1) Long userId) {
        return ResponseEntity
                .status(OK)
                .body(ticketService.getTicketsAssignedToUserWithId(userId));
    }


    @PreAuthorize("@customSecurity.isAuthorized(#userId) || hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    @GetMapping("/author/{userId}") // get all tickets created by specific user
    public ResponseEntity<List<TicketDto>> getTicketsCreatedByUser(@PathVariable("userId") @Min(1) Long userId) {
        return ResponseEntity
                .status(OK)
                .body(ticketService.getTicketsCreatedByUserWithId(userId));
    }


    @PreAuthorize("@customSecurity.isAuthorOrAssignedToProject(#projectId) || hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    @GetMapping("/project/{projectId}") // get all tickets assigned to project
    public ResponseEntity<List<TicketDto>> getTicketsFromProject(@PathVariable("projectId") @Min(1) Long projectId) {
        return ResponseEntity
                .status(OK)
                .body(ticketService.getTicketsFromProjectWithId(projectId));
    }

    @PreAuthorize("@customSecurity.isAuthorOfTicket(#ticketId) || hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/{ticketId}/assign/{userId}") // assign ticket to user
    public ResponseEntity<Boolean> assignUserToTicket(@PathVariable("ticketId") @Min(1) Long ticketId,
                                                      @PathVariable("userId") @Min(1) Long userId) {
        return ResponseEntity
                .status(OK)
                .body(ticketService.assignedUserToTicket(ticketId, userId));
    }

    @PreAuthorize("@customSecurity.isAuthorOrAssignedToTicket(#ticketId) || hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/{ticketId}/new-status")
    public ResponseEntity<TicketDto> changeTicketStatus(@PathVariable("ticketId") Long ticketId,
                                                        @RequestBody @Valid StatusDto statusDto) {
        return ResponseEntity
                .status(OK)
                .body(ticketService.changeStatus(ticketId, statusDto));
    }
}
