package dev.bugtracker.demo.service;

import dev.bugtracker.demo.exception.ResourceNotFoundException;
import dev.bugtracker.demo.dto.StatusDto;
import dev.bugtracker.demo.dto.TicketDto;
import dev.bugtracker.demo.mapper.TicketMapper;
import dev.bugtracker.demo.model.Comment;
import dev.bugtracker.demo.model.Project;
import dev.bugtracker.demo.model.Ticket;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.repository.CommentRepo;
import dev.bugtracker.demo.repository.ProjectRepo;
import dev.bugtracker.demo.repository.TicketRepo;
import dev.bugtracker.demo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class TicketService {
    private final TicketRepo ticketRepo;
    private final UserRepo userRepo;
    private final ProjectRepo projectRepo;
    private final AuthService authService;
    private final CommentRepo commentRepo;
    private final TicketMapper mapper;

    private Set<User> getAssignedUserFromTicketDto(TicketDto ticketDto) {
        Set<Long> usersIds = new HashSet<>();
        ticketDto.getAssignedUsers().forEach(
                userSimpleDto -> usersIds.add(userSimpleDto.getId())
        );

        return new HashSet<>(userRepo.findAllById(usersIds));
    }

    public TicketDto create(TicketDto ticketDto) {
        Project project = projectRepo
                .findById(ticketDto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project with id " + ticketDto.getProjectId() + " " + "not found.")
                );

        Set<User> assignedUsers = getAssignedUserFromTicketDto(ticketDto);

        User currentUser = authService.getCurrentUser();

        log.info("Saving new ticket: {}", ticketDto.getTitle());

        Ticket ticketToSave = mapper.dtoToTicket(ticketDto, currentUser, assignedUsers, project);
        ticketRepo.save(ticketToSave);

        return mapper.ticketToDto(ticketToSave);
    }

    public TicketDto get(Long id) {
        log.info("Fetching ticket by id: {}", id);

        return mapper.ticketToDto(ticketRepo
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + " not found"))
        );
    }

    public TicketDto update(Long id, TicketDto ticketDto) {
        // find ticket by id
        Ticket ticketToUpdate = ticketRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id" + id + " not found"));

        // find new set of assigned users
        Set<User> assignedUsers = getAssignedUserFromTicketDto(ticketDto);

        // update ticket and save
        log.info("Updating ticket with id: {}", id);
        ticketToUpdate.setTitle(ticketDto.getTitle());
        ticketToUpdate.setDescription(ticketDto.getDescription());
        ticketToUpdate.setAssignedUsers(assignedUsers);
        ticketToUpdate.setPriority(ticketDto.getPriority());
        ticketToUpdate.setStatus(ticketDto.getStatus());
        ticketToUpdate.setLastUpdate(Instant.now());

        ticketRepo.save(ticketToUpdate);

        return mapper.ticketToDto(ticketToUpdate);
    }

    public Boolean delete(Long id) {
        Ticket ticketToDelete = ticketRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + " not found"));

        List<Comment> commentsFromTicket = commentRepo.findAllByTicket(ticketToDelete);
        log.info("Deleting comments from ticket with id: {}", id);
        commentRepo.deleteAll(commentsFromTicket);

        log.info("Deleting ticket by id: {}", id);
        ticketRepo.deleteById(id);

        return Boolean.TRUE;
    }

    public Boolean assignedUserToTicket(Long ticketId, Long userId) {

        Ticket ticketToUpdate = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + ticketId + " not found"));

        User userToAdd = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        log.info("Assign user {} to ticket {}", userId, ticketId);
        return ticketToUpdate.getAssignedUsers().add(userToAdd);
    }

    public List<TicketDto> getTicketsAssignedToUserWithId(Long userId) {
        log.info("Fetching tickets assigned to user with id {}", userId);
        return mapper.ticketsToDtoList(ticketRepo.findAllAssignedToUserWithId(userId));
    }

    public List<TicketDto> getTicketsCreatedByUserWithId(Long userId) {
        log.info("Fetching tickets created by user with id: {}.", userId);
        return mapper.ticketsToDtoList(ticketRepo.findAllTicketsCreatedByUserWithId(userId));
    }

    public List<TicketDto> getTicketsFromProjectWithId(Long projectId) {
        log.info("Fetching tickets from project with id: {}.", projectId);
        return mapper.ticketsToDtoList(ticketRepo.findByProjectId(projectId));
    }

    public TicketDto changeStatus(Long ticketId, StatusDto statusDto) {
        Ticket ticketToUpdate = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + ticketId + " not found"));

        ticketToUpdate.setStatus(statusDto.getStatus());
        ticketRepo.save(ticketToUpdate);

        return mapper.ticketToDto(ticketToUpdate);
    }
}
