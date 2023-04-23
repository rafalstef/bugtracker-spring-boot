package dev.bugtracker.demo.service;


import dev.bugtracker.demo.exception.ResourceNotFoundException;
import dev.bugtracker.demo.dto.ProjectDto;
import dev.bugtracker.demo.mapper.ProjectMapper;
import dev.bugtracker.demo.model.Project;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.repository.ProjectRepo;
import dev.bugtracker.demo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class ProjectService {
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final AuthService authService;
    private final TicketService ticketService;
    private final ProjectMapper mapper;

    private Set<User> getUserFromProjectDto(ProjectDto projectDto) {
        Set<Long> usersIds = new HashSet<>();
        projectDto.getUsers().forEach(
                userSimpleDto -> usersIds.add(userSimpleDto.getId())
        );

        return new HashSet<>(userRepo.findAllById(usersIds));
    }

    public ProjectDto create(ProjectDto projectDto) {
        User currentUser = authService.getCurrentUser();

        Set<User> usersToAssigned = getUserFromProjectDto(projectDto);

        log.info("Saving new project {}", projectDto.getName());

        Project projectToSave = mapper.dtoToProject(projectDto, currentUser, usersToAssigned);

        projectRepo.save(projectToSave);
        return mapper.projectToDto(projectToSave);
    }

    public List<ProjectDto> list() {
        return mapper.listToDtoList(projectRepo.findAll());
    }

    public ProjectDto get(Long id) {
        log.info("Fetching project with id: {}", id);
        return mapper.projectToDto(projectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found."))
        );
    }

    public ProjectDto update(Long id, ProjectDto projectDto) {
        Project projectToUpdate = projectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found."));

        Set<User> usersToAssigned = getUserFromProjectDto(projectDto);

        log.info("Updating project with id: {}", id);
        projectToUpdate.setName(projectDto.getName());
        projectToUpdate.setDescription(projectDto.getDescription());
        projectToUpdate.setTargetEndDate(projectDto.getTargetEndDate());
        projectToUpdate.setActualEndDate(projectDto.getActualEndDate());
        projectToUpdate.setUsers(usersToAssigned);

        projectRepo.save(projectToUpdate);

        return mapper.projectToDto(projectToUpdate);
    }

    public Boolean delete(Long id) {
        Project projectToDelete = projectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found."));

        projectToDelete.getTickets().forEach(ticket -> {
            log.info("Deleting ticket with id: {}", ticket.getId());
            ticketService.delete(ticket.getId());
        });

        projectRepo.delete(projectToDelete);
        log.info("Deleting project with id: {}", id);
        return Boolean.TRUE;
    }

    public Boolean addUser(Long projectId, Long userId) {
        Project projectToUpdate = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + projectId + " not found."));

        User userToAdd = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found."));

        log.info("Add user with id {} to project with id {}", userId, projectId);
        return projectToUpdate.getUsers().add(userToAdd);
    }


    public List<ProjectDto> getProjectsAssignedToUser(Long userId) {
        log.info("Fetch projects assigned to user with id: {}", userId);
        return mapper.listToDtoList(projectRepo.findWithAssignedUser(userId));
    }

    public List<ProjectDto> getProjectsCreatedByUserWithId(Long userId) {
        log.info("Fetch projects created by user with id: {}", userId);
        return mapper.listToDtoList(projectRepo.findAllCreatedBy(userId));
    }

}
