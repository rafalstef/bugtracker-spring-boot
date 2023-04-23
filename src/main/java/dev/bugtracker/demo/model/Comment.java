package dev.bugtracker.demo.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity()
public class Comment {

    @Id
    @SequenceGenerator(name = "COM_SEQ", sequenceName = "COM_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COM_SEQ")
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
    private User writtenBy;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id", updatable = false)
    private Ticket ticket;

    @Column(name = "content", updatable = false, nullable = false)
    private String content;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;
}
