package dev.bugtracker.demo.service;

import dev.bugtracker.demo.exception.ResourceNotFoundException;
import dev.bugtracker.demo.dto.CommentDto;
import dev.bugtracker.demo.mapper.CommentMapper;
import dev.bugtracker.demo.model.Comment;
import dev.bugtracker.demo.model.Ticket;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.repository.CommentRepo;
import dev.bugtracker.demo.repository.TicketRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepo commentRepo;
    private final TicketRepo ticketRepo;
    private final CommentMapper mapper;
    private final AuthService authService;

    public CommentDto save(CommentDto commentDto) {
        log.info("Saving new commentPostDto with id: {}", commentDto.getId());

        Long ticketId = commentDto.getTicketId();
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + ticketId + " not found in the " +
                        "database."));

        User user = authService.getCurrentUser();

        Comment commentToSave = Comment.builder()
                .writtenBy(user)
                .ticket(ticket)
                .content(commentDto.getContent())
                .createdAt(Instant.now())
                .build();

        commentRepo.save(commentToSave);
        return mapper.commentToDto(commentToSave);
    }

    public List<CommentDto> getAllTicketComments(Long ticketId) {
        log.info("Fetching comments from ticket with id {}", ticketId);
        return mapper.toDtoList(commentRepo.findByTicketId(ticketId));
    }

    public Boolean delete(Long id) {
        log.info("Deleting comment with id {}", id);
        commentRepo.deleteById(id);
        return Boolean.TRUE;
    }


}
