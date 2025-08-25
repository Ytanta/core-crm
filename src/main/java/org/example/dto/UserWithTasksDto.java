package org.example.dto;


import lombok.*;
import org.example.entity.Role;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWithTasksDto {
    private UUID id;
    private String name;
    private String email;
    private Role role;

    private List<TaskShortDto> tasks;
}
