package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.TaskStatus;
import org.example.entity.Role;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.repository.BuildingRepository;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoTaskService {
    private static final int DEFAULT_DUE_DAYS = 1;

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public void createManagerContactTasks() {
        List<User> managers = userRepository.findAllByRole(Role.MANAGER);

        for (User manager : managers) {
            boolean exists = taskRepository.existsByOwnerAndStatus(manager, TaskStatus.NEW);
            if (!exists) {
                Task task = new Task();
                task.setOwner(manager);
                task.setDescription("Связаться с владельцем для уточнения возможной продажи");
                task.setStatus(TaskStatus.NEW);
                task.setDueDate(LocalDate.now().plusDays(DEFAULT_DUE_DAYS));
                taskRepository.save(task);
            }
        }
    }
}