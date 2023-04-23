package dev.bugtracker.demo.dto;

import dev.bugtracker.demo.enumeration.UserRole;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserGetDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private LocalDate registeredAt;
}
