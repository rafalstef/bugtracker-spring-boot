package dev.bugtracker.demo.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProjectDto {

    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    private Set<UserSimpleDto> users = new HashSet<>();

    @NotNull(message = "Start date must be not null")
    private LocalDate startDate;

    @NotNull(message = "Target end date must be not null")
    private LocalDate targetEndDate;

    private LocalDate actualEndDate;

    private UserSimpleDto createdBy;
}
