package dev.bugtracker.demo.repository;

import dev.bugtracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // SELECT * FROM user u WHERE u.id IN (SELECT user_id FROM project_user WHERE project_id = ?1)
    @Query(value = "select u from User u inner join u.projects p where p.id = ?1")
    Set<User> findUsersAssignedToProjectById(Long projectId);

    // SELECT * FROM user u WHERE u.id IN (SELECT user_id FROM ticket_user WHERE ticket_id = ?1)
    @Query(value = "select u from User u inner join u.assignedTickets t where t.id = ?1")
    Set<User> findByAssignedTicket(Long ticketId);
}
