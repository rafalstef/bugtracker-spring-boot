package dev.bugtracker.demo.mapper;

import dev.bugtracker.demo.dto.UserGetDto;
import dev.bugtracker.demo.dto.UserSimpleDto;
import dev.bugtracker.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", expression = "java(user.getId())")
    @Mapping(target = "name", expression = "java(String.format(\"%s %s\", user.getFirstName(), user.getLastName()))")
    UserSimpleDto userToSimpleDto(User user);

    UserGetDto userToUserGetDto(User user);

    List<UserGetDto> usersToUserGetDtoList(List<User> users);

    Set<UserSimpleDto> toSimpleDtoSet(Set<User> users);
}
