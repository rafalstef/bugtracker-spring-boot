package dev.bugtracker.demo.dto;

import dev.bugtracker.demo.enumeration.UserRole;
import dev.bugtracker.demo.validator.DateBirthConstraint;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserPostDto {

    @Email(
            regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid email address format"
    )
    @NotNull(message = "Email is required.")
    private String email;

    @NotNull(message = "Password is required.")
    private String password;

    @NotNull(message = "First name is required.")
    private String firstName;

    @NotNull(message = "Last name is required.")
    private String lastName;

    @NotNull(message = "Birth date is required.")
    @DateBirthConstraint
    private LocalDate birthDate;

    @NotNull(message = "Role is required.")
    private UserRole role;
}
