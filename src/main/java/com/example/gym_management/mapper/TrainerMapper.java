package com.example.gym_management.mapper;

import com.example.gym_management.dto.ClassTypeDTO;
import com.example.gym_management.dto.TrainerDTO;
import com.example.gym_management.dto.TrainerRequest;
import com.example.gym_management.dto.TrainerResponse;
import com.example.gym_management.entity.ClassType;
import com.example.gym_management.entity.Trainer;
import com.example.gym_management.repository.ClassTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainerMapper {

    private final ClassTypeRepository classTypeRepository;
    private final ClassTypeMapper classTypeMapper;

    public TrainerDTO toSimpleDto(Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        return new TrainerDTO(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName()
        );
    }

    public TrainerResponse toResponse(Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        return new TrainerResponse(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                calculateScheduledClassCount(trainer),
                null
        );
    }

    public TrainerResponse toResponseWithClassTypes(Trainer trainer) {
        if (trainer == null) {
            return null;
        }

        List<ClassTypeDTO> classTypeDTOs = trainer.getClassTypes() != null
                ? trainer.getClassTypes().stream()
                .map(classTypeMapper::toSimpleDto)
                .collect(Collectors.toList())
                : null;

        return new TrainerResponse(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                calculateScheduledClassCount(trainer),
                classTypeDTOs
        );
    }

    public TrainerResponse toResponseWithoutCount(Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        return new TrainerResponse(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                null,
                null
        );
    }

    public List<TrainerResponse> toResponseListWithoutCount(List<Trainer> trainers) {
        return trainers.stream()
                .map(this::toResponseWithoutCount)
                .collect(Collectors.toList());
    }

    public Trainer toEntity(TrainerRequest request) {
        Trainer trainer = new Trainer(
                request.getFirstName(),
                request.getLastName()
        );

        if (request.getClassTypeIds() != null && !request.getClassTypeIds().isEmpty()) {
            Set<ClassType> classTypes = resolveClassTypes(request.getClassTypeIds());
            trainer.setClassTypes(classTypes);
        }

        return trainer;
    }

    public void updateEntity(TrainerRequest request, Trainer trainer) {
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());

        if (request.getClassTypeIds() != null) {
            if (request.getClassTypeIds().isEmpty()) {
                trainer.getClassTypes().clear();
            } else {
                Set<ClassType> classTypes = resolveClassTypes(request.getClassTypeIds());
                trainer.setClassTypes(classTypes);
            }
        }
    }

    private Integer calculateScheduledClassCount(Trainer trainer) {
        return trainer.getScheduledClasses() != null
                ? trainer.getScheduledClasses().size()
                : 0;
    }

    private Set<ClassType> resolveClassTypes(Set<Long> classTypeIds) {
        Set<ClassType> classTypes = new HashSet<>(
                classTypeRepository.findAllById(classTypeIds)
        );
        if (classTypes.size() != classTypeIds.size()) {
            throw new IllegalArgumentException("One or more class type IDs are invalid");
        }
        return classTypes;
    }
}
