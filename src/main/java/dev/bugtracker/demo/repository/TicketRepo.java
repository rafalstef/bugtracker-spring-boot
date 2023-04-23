package dev.bugtracker.demo.repository;

import dev.bugtracker.demo.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t WHERE t.project.id = ?1")
    List<Ticket> findByProjectId(Long projectId);

    // SELECT * FROM ticket t WHERE t.id IN ( SELECT ticket_id FROM ticket_user  WHERE user_id = ?1 )
    @Query("select t from Ticket t inner join t.assignedUsers u where u.id = ?1")
    List<Ticket> findAllAssignedToUserWithId(Long userId);

    @Query("select t from Ticket t where t.createdBy.id = ?1")
    List<Ticket> findAllTicketsCreatedByUserWithId(Long userId);



}
