package dev.bugtracker.demo.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Project {

    @Id
    @SequenceGenerator(name = "PROJECT_SEQ", sequenceName = "PROJECT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECT_SEQ")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    private Set<Ticket> tickets;

    @Column(name = "start_date", nullable = false, updatable = false)
    private LocalDate startDate;

    @Column(name = "target_end_date", nullable = false, updatable = false)
    private LocalDate targetEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "id", updatable = false)
    private User createdBy;
}
