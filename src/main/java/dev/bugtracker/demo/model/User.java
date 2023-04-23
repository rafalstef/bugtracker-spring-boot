package dev.bugtracker.demo.model;

import dev.bugtracker.demo.enumeration.UserRole;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(name = "user_email_unique", columnNames = "email")})
public class User {

    @Id
    @SequenceGenerator(name = "USER_SEQ", sequenceName = "USER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, length = 250)
    private String email;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @ManyToMany(mappedBy = "users")
    private Set<Project> projects = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy")
    private Set<Ticket> createdTickets;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy")
    private Set<Project> createdProjects;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "assignedUsers")
    private Set<Ticket> assignedTickets = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writtenBy")
    private Set<Comment> writtenComments = new HashSet<>();

    @Column(name = "birth_date", nullable = false, updatable = false)
    private LocalDate birthDate;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDate registeredAt;

    private UserRole role;

    private Boolean isEnabled = Boolean.FALSE;

}
