package org.example.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingWithTasksDto {
    private UUID id;
    private String address;
    private String type;
    private List<TaskShortDto> tasks;
}