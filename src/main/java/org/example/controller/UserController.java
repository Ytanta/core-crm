package org.example.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserDto;
import org.example.dto.UserWithTasksDto;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.specification.SpecificationsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/{id}/with-tasks")
    public ResponseEntity<UserWithTasksDto> getByIdWithTasks(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findByIdWithTasks(id));
    }

        @PostMapping
        public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto dto) {
            UserDto created = userService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable UUID id, @Valid @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String search) {
        List<UserDto> users = userService.searchUsers(search);
        return ResponseEntity.ok(users);
    }
}