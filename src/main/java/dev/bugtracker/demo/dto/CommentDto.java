package dev.bugtracker.demo.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CommentDto {

    private Long id;

    private UserSimpleDto user;

    @NotNull(message = "Ticket id cannot be null")
    private Long ticketId;

    @NotBlank(message = "Comment content cannot be blank")
    private String content;

    private Instant createdAt;
}