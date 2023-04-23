package dev.bugtracker.demo.email;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class EmailDetails {
    private String recipient;
    private String msgBody;
    private String subject;
}