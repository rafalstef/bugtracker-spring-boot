package dev.bugtracker.demo.dto;

import dev.bugtracker.demo.enumeration.Priority;
import dev.bugtracker.demo.enumeration.Status;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TicketDto {

    private Long id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    private UserSimpleDto createdBy;

    private Set<UserSimpleDto> assignedUsers;

    @NotNull(message = "Project id cannot be null")
    private Long projectId;

    @NotNull(message = "Priority cannot be null")
    private Priority priority;

    private Status status = Status.TO_DO;

    private Instant createdOn;

    private Instant lastUpdate;
}
