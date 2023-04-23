package dev.bugtracker.demo.mapper;

import dev.bugtracker.demo.dto.ProjectDto;
import dev.bugtracker.demo.model.Project;
import dev.bugtracker.demo.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {UserMapper.class})
public abstract class ProjectMapper {

    protected final UserMapper userMapper = new UserMapperImpl();

    @Mapping(target = "createdBy", expression = "java(userMapper.userToSimpleDto(project.getCreatedBy()))")
    @Mapping(target = "users", expression = "java(userMapper.toSimpleDtoSet(project.getUsers()))")
    public abstract ProjectDto projectToDto(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "users", source = "users")
    @Mapping(target = "startDate", expression = "java(java.time.LocalDate.now())")
    public abstract Project dtoToProject(ProjectDto projectDto, User createdBy, Set<User> users);

    public abstract List<ProjectDto> listToDtoList(List<Project> projects);
    
}
