package dev.bugtracker.demo;

import dev.bugtracker.demo.config.RsaKeyProperties;
import dev.bugtracker.demo.enumeration.Priority;
import dev.bugtracker.demo.enumeration.Status;
import dev.bugtracker.demo.enumeration.UserRole;
import dev.bugtracker.demo.model.Comment;
import dev.bugtracker.demo.model.Project;
import dev.bugtracker.demo.model.Ticket;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.repository.CommentRepo;
import dev.bugtracker.demo.repository.ProjectRepo;
import dev.bugtracker.demo.repository.TicketRepo;
import dev.bugtracker.demo.repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(RsaKeyProperties.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(ProjectRepo projectRepo,
                                        UserRepo userRepo,
                                        TicketRepo ticketRepo,
                                        CommentRepo commentRepo,
                                        PasswordEncoder passwordEncoder) {


        return args -> {
            User user1 = User.builder()
                    .email("jan.kowalski@email.com")
                    .firstName("Jan")
                    .lastName("Kowalski")
                    .password(passwordEncoder.encode("password"))
                    .birthDate(LocalDate.now().minusYears(27))
                    .registeredAt(LocalDate.now())
                    .role(UserRole.ADMIN)
                    .isEnabled(Boolean.TRUE)
                    .build();


            User user2 = User.builder()
                    .email("maria.bomba@email.com")
                    .firstName("Maria")
                    .lastName("Bomba")
                    .password(passwordEncoder.encode("password"))
                    .birthDate(LocalDate.now().minusYears(22))
                    .registeredAt(LocalDate.now())
                    .role(UserRole.DEVELOPER)
                    .isEnabled(Boolean.TRUE)
                    .build();

            User user3 = User.builder()
                    .email("mariusz.kowal@email.com")
                    .firstName("Mariusz")
                    .lastName("Kowal")
                    .password(passwordEncoder.encode("password"))
                    .birthDate(LocalDate.now().minusYears(22))
                    .registeredAt(LocalDate.now())
                    .role(UserRole.TESTER)
                    .isEnabled(Boolean.TRUE)
                    .build();

            User user4 = User.builder()
                    .email("kamil.boberek@email.com")
                    .firstName("Kamil")
                    .lastName("Boberek")
                    .password(passwordEncoder.encode("password"))
                    .birthDate(LocalDate.now().minusYears(22))
                    .registeredAt(LocalDate.now())
                    .role(UserRole.MANAGER)
                    .isEnabled(Boolean.TRUE)
                    .build();

            Project project1 = new Project(
                    null,
                    "First project",
                    "Bug tracking",
                    new HashSet<>(Arrays.asList(user1, user2, user3, user4)),
                    null,
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now(),
                    user1
            );


            Ticket ticket1 = new Ticket(
                    null,
                    "First ticket",
                    "Desc of first ticket",
                    user3,
                    project1,
                    null,
                    new HashSet<>(Arrays.asList(user3, user2)),
                    Priority.LOW,
                    Status.TO_DO,
                    Instant.now(),
                    Instant.now()
            );

            Comment comment1 = new Comment(
                    null,
                    user2,
                    ticket1,
                    "Some comment",
                    Instant.now()
            );

            userRepo.saveAll(new ArrayList<User>(Arrays.asList(user1, user2, user3, user4)));
            projectRepo.save(project1);
            ticketRepo.save(ticket1);
            commentRepo.save(comment1);
        };
    }

}
