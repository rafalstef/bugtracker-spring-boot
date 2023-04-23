package dev.bugtracker.demo.controller;

import dev.bugtracker.demo.dto.CommentDto;
import dev.bugtracker.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PreAuthorize("@customSecurity.isAuthorOrAssignedToTicket(#id) || hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<CommentDto>> getTicketComments(@PathVariable("ticketId") @Min(1) Long id) {
        return ResponseEntity
                .status(OK)
                .body(commentService.getAllTicketComments(id));
    }

    @PreAuthorize("@customSecurity.isAuthorOrAssignedToTicket(#dto.ticketId)")
    @PostMapping()
    public ResponseEntity<CommentDto> saveComment(@RequestBody CommentDto dto) {
        return ResponseEntity
                .status(CREATED)
                .body(commentService.save(dto));
    }

    @PreAuthorize("@customSecurity.isAuthorOfComment(#id) || hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteComment(@PathVariable Long id) {
        Boolean isDeleted = commentService.delete(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id);
        }
        return ResponseEntity.status(OK).body(id);
    }

}
