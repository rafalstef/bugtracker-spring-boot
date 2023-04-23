package dev.bugtracker.demo.mapper;

import dev.bugtracker.demo.dto.TicketDto;
import dev.bugtracker.demo.model.Project;
import dev.bugtracker.demo.model.Ticket;
import dev.bugtracker.demo.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {UserMapper.class})
public abstract class TicketMapper {

    protected UserMapper userMapper = new UserMapperImpl();

    @Mapping(target = "projectId", expression = "java(ticket.getProject().getId())")
    @Mapping(target = "createdBy", expression = "java(userMapper.userToSimpleDto(ticket.getCreatedBy()))")
    @Mapping(target = "assignedUsers", expression = "java(userMapper.toSimpleDtoSet(ticket.getAssignedUsers()))")
    public abstract TicketDto ticketToDto(Ticket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "description", expression = "java(ticketDto.getDescription())")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "assignedUsers", source = "assignedUsers")
    @Mapping(target = "project", source = "project")
    @Mapping(target = "priority", expression = "java(ticketDto.getPriority())")
    @Mapping(target = "status", expression = "java(ticketDto.getStatus())")
    @Mapping(target = "createdOn", expression = "java(java.time.Instant.now())")
    @Mapping(target = "lastUpdate", expression = "java(java.time.Instant.now())")
    public abstract Ticket dtoToTicket(TicketDto ticketDto, User createdBy, Set<User> assignedUsers, Project project);

    public abstract List<TicketDto> ticketsToDtoList(List<Ticket> tickets);

}
