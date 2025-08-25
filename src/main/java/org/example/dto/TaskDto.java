package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private UUID id;

    @NotBlank(message = "Description is required")
    private String description;

    private LocalDate dueDate;

    @NotNull(message = "Status is required")
    private TaskStatus status;

    private UUID managerId;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    private UUID buyerId;

    @NotNull(message = "Building ID is required")
    private UUID buildingId;
}