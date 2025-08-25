package org.example.mapper;


import org.example.dto.TaskShortDto;
import org.example.dto.UserDto;
import org.example.dto.UserWithTasksDto;
import org.example.entity.Task;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = TaskMapper.class)
public interface UserMapper {

    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    void updateEntityFromDto(UserDto dto, @MappingTarget User entity);

    UserWithTasksDto toUserWithTasksDto(User user);
}