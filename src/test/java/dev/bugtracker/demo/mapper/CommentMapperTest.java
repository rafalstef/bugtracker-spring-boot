package dev.bugtracker.demo.mapper;

import dev.bugtracker.demo.dto.CommentDto;
import dev.bugtracker.demo.model.Comment;
import dev.bugtracker.demo.model.Ticket;
import dev.bugtracker.demo.model.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentMapperTest {

    private final CommentMapper sut = new CommentMapperImpl();

    @Test
    void commentToCommentGetDto() {
        User user = User.builder().id(2L).firstName("John").lastName("Doe").build();
        Ticket ticket = Ticket.builder().id(12L).build();
        Comment comment = Comment.builder()
                .id(123L)
                .content("Comment content")
                .createdAt(Instant.now())
                .ticket(ticket)
                .writtenBy(user)
                .build();

        CommentDto commentDto = sut.commentToDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getContent(), commentDto.getContent());
        assertEquals(comment.getCreatedAt(), commentDto.getCreatedAt());
        assertEquals(user.getFirstName() + " " + user.getLastName(), commentDto.getUser().getName());
        assertEquals(user.getId(), commentDto.getUser().getId());
        assertEquals(ticket.getId(), commentDto.getTicketId());
    }

    @Test
    void commentsToCommentDtoList() {
        int size = 10;
        List<Comment> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(new Comment());
        }
        List<CommentDto> dtoList = sut.toDtoList(list);

        assertNotNull(dtoList);
        assertEquals(size, dtoList.size());
    }
}