package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.example.dto.BuildingDto;
import org.example.dto.BuildingWithTasksDto;
import org.example.entity.Building;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.mapper.BuildingMapper;
import org.example.repository.BuildingRepository;
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
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private final BuildingMapper buildingMapper;

    public List<BuildingDto> findAll() {
        return buildingRepository.findAll().stream()
                .map(buildingMapper::toDto)
                .toList();
    }

    public BuildingDto findById(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Building not found with id: " + id));
        return buildingMapper.toDto(building);
    }

    public BuildingWithTasksDto findByIdWithTasks(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Building not found with id: " + id));
        return buildingMapper.toBuildingWithTasksDto(building);
    }

    @Transactional
    public BuildingDto create(BuildingDto dto) {

        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getOwnerId()));

        if (owner.getRole() != Role.OWNER) {
            throw new IllegalArgumentException("User must have role OWNER");
        }

        Building building = Building.builder()
                .address(dto.getAddress())
                .type(dto.getType())
                .owner(owner)
                .build();

        Building saved = buildingRepository.save(building);
        return buildingMapper.toDto(saved);
    }

    @Transactional
    public BuildingDto update(UUID id, BuildingDto dto) {
        Building existing = buildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Building not found with id: " + id));

        if (dto.getOwnerId() != null) {
            User newOwner = userRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getOwnerId()));

            if (newOwner.getRole() != Role.OWNER) {
                throw new IllegalArgumentException("New owner must have role OWNER");
            }
            existing.setOwner(newOwner);
        }

        existing.setAddress(dto.getAddress());
        existing.setType(dto.getType());

        Building updated = buildingRepository.save(existing);
        return buildingMapper.toDto(updated);
    }



    @Transactional
    public void delete(UUID id) {
        if (!buildingRepository.existsById(id)) {
            throw new EntityNotFoundException("Building not found with id: " + id);
        }
        buildingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BuildingDto> searchBuildings(String search) {
        SpecificationsBuilder<Building> builder = new SpecificationsBuilder<>();
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");

        Set<String> allowedFields = Set.of("id", "address", "type", "owner");

        while (matcher.find()) {
            String field = matcher.group(1);
            if (!allowedFields.contains(field)) {
                throw new IllegalArgumentException("Поле '" + field + "' не существует для поиска");
            }
            builder.with(field, matcher.group(2), matcher.group(3), false);
        }

        Specification<Building> spec = builder.build();
        return buildingRepository.findAll(spec)
                .stream()
                .map(buildingMapper::toDto)
                .toList();
    }
}