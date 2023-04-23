package dev.bugtracker.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSimpleDto {

    @NotNull(message = "Id cannot be null")
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;
}
