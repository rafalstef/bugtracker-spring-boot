package dev.bugtracker.demo.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "refresh_token", uniqueConstraints = {@UniqueConstraint(name = "refresh_token_unique", columnNames = "token")})
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

}
