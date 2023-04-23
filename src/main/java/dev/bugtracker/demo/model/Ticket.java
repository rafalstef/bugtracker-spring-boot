package dev.bugtracker.demo.model;

import dev.bugtracker.demo.enumeration.Priority;
import dev.bugtracker.demo.enumeration.Status;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Ticket {

    @Id
    @SequenceGenerator(name = "TICKET_SEQ", sequenceName = "TICKET_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TICKET_SEQ")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
    private User createdBy;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ticket")
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ticket_user",
            joinColumns = @JoinColumn(
                    name = "ticket_id", referencedColumnName = "id",
                    nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id",
                    nullable = false, updatable = false)
    )
    private Set<User> assignedUsers = new HashSet<>();

    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;

    @Column(name = "last_update", nullable = false)
    private Instant lastUpdate;

}
