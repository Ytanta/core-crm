package org.example.mapper;


import org.example.dto.TaskDto;
import org.example.dto.TaskShortDto;
import org.example.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;


@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "building", ignore = true)
    Task toEntity(TaskDto dto);

    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "buyer.id", target = "buyerId")
    @Mapping(source = "building.id", target = "buildingId")
    TaskDto toDto(Task entity);

    void updateEntityFromDto(TaskDto dto, @MappingTarget Task entity);

    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "buyer.id", target = "buyerId")
    TaskShortDto toShortDto(Task task);

    List<TaskShortDto> toShortDtos(List<Task> tasks);
}