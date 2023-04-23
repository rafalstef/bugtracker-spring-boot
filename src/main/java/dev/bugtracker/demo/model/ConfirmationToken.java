package dev.bugtracker.demo.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "confirmation_token")
public class ConfirmationToken {
    @Id
    @SequenceGenerator(
            name = "confirmation_token_sequence",
            sequenceName = "confirmation_token_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "confirmation_token_sequence"
    )
    private Long id;
    private String token;
    @OneToOne(fetch = LAZY)
    private User user;
    private Instant expiryDate;
}