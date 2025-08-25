package org.example.mapper;

import org.example.dto.BuildingDto;
import org.example.dto.BuildingWithTasksDto;
import org.example.dto.TaskShortDto;
import org.example.entity.Building;
import org.example.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    Building toEntity(BuildingDto dto)
            ;
    @Mapping(source = "owner.id", target = "ownerId")
    BuildingDto toDto(Building entity);

    void updateEntityFromDto(BuildingDto dto, @MappingTarget Building entity);

    List<TaskShortDto> tasksToShortDtos(List<Task> tasks);

    BuildingWithTasksDto toBuildingWithTasksDto(Building building);
}