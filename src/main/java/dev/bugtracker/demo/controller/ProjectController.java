package dev.bugtracker.demo.controller;

import dev.bugtracker.demo.dto.ProjectDto;
import dev.bugtracker.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PreAuthorize("@customSecurity.isAuthorOrAssignedToProject(#id) || hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable("id") @Min(1) Long id) {
        return ResponseEntity
                .status(OK)
                .body(projectService.get(id));
    }

    @PreAuthorize("@customSecurity.isAuthorized(#userId) || hasAnyAuthority('SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ProjectDto>> getProjectsAssignedToUser(@PathVariable(value = "userId")
                                                                      @Min(1) Long userId) {
        return ResponseEntity
                .status(OK)
                .body(projectService.getProjectsAssignedToUser(userId));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @GetMapping("/create/{userId}")
    public ResponseEntity<List<ProjectDto>> getProjectsCreatedByUser(@PathVariable("userId") @Min(1) Long userId) {
        return ResponseEntity
                .status(OK)
                .body(projectService.getProjectsCreatedByUserWithId(userId));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(projectService.list());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_MANAGER', 'SCOPE_ADMIN')")
    @PostMapping()
    public ResponseEntity<ProjectDto> saveProject(@RequestBody @Valid ProjectDto projectDto) {
        return ResponseEntity
                .status(CREATED)
                .body(projectService.create(projectDto));
    }

    @PreAuthorize("@customSecurity.isAuthorOfProject(#id) || hasAuthority('SCOPE_ADMIN')")
    @PutMapping(path = "/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable("id") @Min(1) Long id,
                                                    @RequestBody @Valid ProjectDto projectDto) {
        return ResponseEntity
                .status(OK)
                .body(projectService.update(id, projectDto));
    }

    @PreAuthorize("@customSecurity.isAuthorOfProject(#id) || hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(OK)
    public ResponseEntity<Long> deleteProject(@PathVariable("id") @Min(1) Long id) {
        Boolean isDeleted = projectService.delete(id);

        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id);
        }
        return ResponseEntity.status(OK).body(id);
    }

    @PreAuthorize("@customSecurity.isAuthorOfProject(#projectId) || hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Boolean> addUserToProject(@PathVariable("projectId") @Min(1) Long projectId,
                                                    @PathVariable("userId") @Min(1) Long userId) {
        return ResponseEntity
                .status(OK)
                .body(projectService.addUser(projectId, userId));
    }

}
