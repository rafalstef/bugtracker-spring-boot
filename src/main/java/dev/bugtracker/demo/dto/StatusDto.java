package dev.bugtracker.demo.dto;

import dev.bugtracker.demo.enumeration.Status;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatusDto {
    @NotNull("Status cannot be null")
    Status status;
}
