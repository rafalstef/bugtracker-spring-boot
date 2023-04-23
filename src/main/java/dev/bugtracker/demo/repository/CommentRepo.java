package dev.bugtracker.demo.repository;

import dev.bugtracker.demo.model.Comment;
import dev.bugtracker.demo.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.ticket.id = ?1")
    List<Comment> findByTicketId(Long ticketId);
    List<Comment> findAllByTicket(Ticket ticket);
}
