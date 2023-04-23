package dev.bugtracker.demo.mapper;

import dev.bugtracker.demo.dto.CommentDto;
import dev.bugtracker.demo.model.Comment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {UserMapper.class})
public abstract class CommentMapper {

    protected UserMapper userMapper = new UserMapperImpl();
    @Mapping(target = "user", expression = "java(userMapper.userToSimpleDto(comment.getWrittenBy()))")
    @Mapping(target = "ticketId", source = "comment.ticket.id")
    public abstract CommentDto commentToDto(Comment comment);

    public abstract List<CommentDto> toDtoList(List<Comment> comments);
}
