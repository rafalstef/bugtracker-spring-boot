package dev.bugtracker.demo.mapper;

import dev.bugtracker.demo.dto.ProjectDto;
import dev.bugtracker.demo.model.Project;
import dev.bugtracker.demo.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectMapperTest {

    private final ProjectMapper sut = new ProjectMapperImpl();

    @Test
    void projectToDto() {
        User user = User.builder().id(12L).firstName("John").lastName("Doe").build();
        User user2 = User.builder().id(13L).firstName("Michael").lastName("Scott").build();
        Project project = Project.builder()
                .id(123L)
                .name("Project")
                .description("Description")
                .users(new HashSet<>(Arrays.asList(user, user2)))
                .startDate(LocalDate.now())
                .targetEndDate(LocalDate.now().plus(10, ChronoUnit.DAYS))
                .createdBy(user)
                .build();

        ProjectDto dto = sut.projectToDto(project);

        assertEquals(project.getId(), dto.getId());
        assertEquals(project.getName(), dto.getName());
        assertEquals(project.getDescription(), dto.getDescription());
        assertEquals(project.getUsers().size(), dto.getUsers().size());
        assertEquals(project.getStartDate(), dto.getStartDate());
        assertEquals(project.getActualEndDate(), dto.getActualEndDate());
        assertEquals(project.getTargetEndDate(), dto.getTargetEndDate());
        assertEquals(project.getCreatedBy().getId(), dto.getCreatedBy().getId());
    }

    @Test
    void dtoToProject() {
    }

    @Test
    void listToDtoList() {
    }
}