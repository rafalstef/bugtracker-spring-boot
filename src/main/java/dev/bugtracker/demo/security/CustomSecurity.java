package dev.bugtracker.demo.security;

import dev.bugtracker.demo.model.Comment;
import dev.bugtracker.demo.model.Project;
import dev.bugtracker.demo.model.Ticket;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomSecurity {

    private AuthService authService;

    public boolean isAuthorized(Long userId) {
        return userId.equals(authService.getCurrentUser().getId());
    }

    public boolean isAuthorOrAssignedToProject(Long projectId) {
        return (isAuthorOfProject(projectId) || isAssignedToProject(projectId));
    }

    public boolean isAuthorOfProject(Long projectId) {
        User currentUser = authService.getCurrentUser();
        return currentUser.getCreatedProjects()
                .stream()
                .map(Project::getId)
                .toList()
                .contains(projectId);
    }

    public boolean isAssignedToProject(Long projectId) {
        User currentUser = authService.getCurrentUser();
        return currentUser.getProjects()
                .stream()
                .map(Project::getId)
                .toList()
                .contains(projectId);
    }

    public boolean isAuthorOrAssignedToTicket(Long ticketId) {
        return (isAuthorOfTicket(ticketId) || isAssignedToTicket(ticketId));
    }

    public boolean isAuthorOfTicket(Long ticketId) {
        User currentUser = authService.getCurrentUser();
        return currentUser.getCreatedTickets()
                .stream()
                .map(Ticket::getId)
                .toList()
                .contains(ticketId);
    }

    public boolean isAssignedToTicket(Long ticketId) {
        User currentUser = authService.getCurrentUser();
        return currentUser.getAssignedTickets()
                .stream()
                .map(Ticket::getId)
                .toList()
                .contains(ticketId);
    }

    public boolean isAuthorOfComment(Long commentId) {
        User currentUser = authService.getCurrentUser();
        return currentUser.getWrittenComments()
                .stream()
                .map(Comment::getId)
                .toList()
                .contains(commentId);
    }
}
