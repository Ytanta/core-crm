package org.example.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.TaskShortDto;
import org.example.dto.UserDto;
import org.example.dto.UserWithTasksDto;
import org.example.entity.User;
import org.example.mapper.TaskMapper;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.example.specification.SpecificationsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskMapper taskMapper;

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${APP_B_URL:http://localhost:8081}")
    private String appBUrl;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserWithTasksDto findByIdWithTasks(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        UserWithTasksDto dto = userMapper.toUserWithTasksDto(user);
        dto.setTasks(collectAllTasks(user));
        return dto;
    }


    private List<TaskShortDto> collectAllTasks(User user) {
        return Stream.of(user.getOwnedTasks(), user.getManagedTasks(), user.getBoughtTasks())
                .flatMap(List::stream)
                .map(taskMapper::toShortDto)
                .toList();
    }

    @Transactional
    public UserDto create(UserDto dto) {
        User saved = userRepository.save(userMapper.toEntity(dto));
        UserDto result = userMapper.toDto(saved);

        try {
            restTemplate.postForObject(appBUrl + "/notify", result, String.class);
        } catch (Exception e) {
            System.err.println("Failed to notify App B: " + e.getMessage());
        }

        return result;
    }

    @Transactional
    public UserDto update(UUID id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());
        return userMapper.toDto(userRepository.save(existing));
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(String search) {
        SpecificationsBuilder<User> builder = new SpecificationsBuilder<>();
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");

        Set<String> allowedFields = Set.of("id", "name", "email", "role");

        while (matcher.find()) {
            String field = matcher.group(1);
            if (!allowedFields.contains(field)) {
                throw new IllegalArgumentException("Поле '" + field + "' не существует для поиска");
            }
            builder.with(field, matcher.group(2), matcher.group(3), false);
        }

        Specification<User> spec = builder.build();
        return userRepository.findAll(spec)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
