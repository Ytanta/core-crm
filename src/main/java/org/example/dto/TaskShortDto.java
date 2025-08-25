package org.example.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskShortDto {
    private UUID id;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private UUID managerId;
    private UUID ownerId;
    private UUID buyerId;
}

