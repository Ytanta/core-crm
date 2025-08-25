package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


import org.example.dto.TaskDto;
import org.example.dto.TaskStatus;
import org.example.entity.Building;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.mapper.TaskMapper;
import org.example.repository.BuildingRepository;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.specification.SpecificationsBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;

    public List<TaskDto> findAll() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .toList();
    }

    public TaskDto findById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDto create(TaskDto dto) {
        Task task = taskMapper.toEntity(dto);

        if (dto.getManagerId() != null) {
            task.setManager(userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + dto.getManagerId())));
        }
        task.setOwner(userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with id: " + dto.getOwnerId())));
        if (dto.getBuyerId() != null) {
            task.setBuyer(userRepository.findById(dto.getBuyerId())
                    .orElseThrow(() -> new EntityNotFoundException("Buyer not found with id: " + dto.getBuyerId())));
        }

        task.setBuilding(buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new EntityNotFoundException("Building not found with id: " + dto.getBuildingId())));

        Task saved = taskRepository.save(task);
        return taskMapper.toDto(saved);
    }

    @Transactional
    public TaskDto update(UUID id, TaskDto dto) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        taskMapper.updateEntityFromDto(dto, existing);

        if (dto.getManagerId() != null) {
            existing.setManager(userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + dto.getManagerId())));
        } else {
            existing.setManager(null);
        }

        existing.setOwner(userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with id: " + dto.getOwnerId())));

        if (dto.getBuyerId() != null) {
            existing.setBuyer(userRepository.findById(dto.getBuyerId())
                    .orElseThrow(() -> new EntityNotFoundException("Buyer not found with id: " + dto.getBuyerId())));
        } else {
            existing.setBuyer(null);
        }

        existing.setBuilding(buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new EntityNotFoundException("Building not found with id: " + dto.getBuildingId())));

        try {
            Task saved = taskRepository.save(existing);
            return taskMapper.toDto(saved);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            throw new IllegalStateException("Task was updated concurrently, please retry", e);
        }
    }

    @Transactional
    public void delete(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> search(String search) {
        SpecificationsBuilder<Task> builder = new SpecificationsBuilder<>();
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");

        Set<String> allowedFields = Set.of(
                "id",
                "description",
                "dueDate",
                "status",
                "manager",
                "buyer",
                "owner",
                "building"
        );

        while (matcher.find()) {
            String field = matcher.group(1);
            if (!allowedFields.contains(field)) {
                throw new IllegalArgumentException("Поле '" + field + "' не существует для поиска");
            }
            builder.with(field, matcher.group(2), matcher.group(3), false);
        }

        Specification<Task> spec = builder.build();
        return taskRepository.findAll(spec)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

}