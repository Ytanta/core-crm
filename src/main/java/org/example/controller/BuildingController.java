package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.dto.BuildingDto;
import org.example.dto.BuildingWithTasksDto;
import org.example.service.BuildingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public ResponseEntity<List<BuildingDto>> getAll() {
        return ResponseEntity.ok(buildingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(buildingService.findById(id));
    }

    @GetMapping("/{id}/with-tasks")
    public ResponseEntity<BuildingWithTasksDto> getByIdWithTasks(@PathVariable UUID id) {
        return ResponseEntity.ok(buildingService.findByIdWithTasks(id));
    }

    @PostMapping
    public ResponseEntity<BuildingDto> create(@Valid @RequestBody BuildingDto dto) {
        BuildingDto created = buildingService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuildingDto> update(@PathVariable UUID id, @Valid @RequestBody BuildingDto dto) {
        return ResponseEntity.ok(buildingService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        buildingService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<BuildingDto>> searchBuildings(@RequestParam String search) {
        List<BuildingDto> buildings = buildingService.searchBuildings(search);
        return ResponseEntity.ok(buildings);
    }
}