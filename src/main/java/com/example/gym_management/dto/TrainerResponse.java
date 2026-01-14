package com.example.gym_management.dto;

import com.example.gym_management.entity.Trainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private Integer scheduledClassCount;
    private List<ClassTypeDTO> classTypes;

    public static TrainerResponse fromEntity(Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        return new TrainerResponse(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getScheduledClasses() != null ? trainer.getScheduledClasses().size() : 0,
                null
        );
    }

    public static TrainerResponse fromEntityWithClassTypes(Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        List<ClassTypeDTO> classTypeDTOs = trainer.getClassTypes() != null
                ? trainer.getClassTypes().stream()
                    .map(ClassTypeDTO::fromEntity)
                    .collect(Collectors.toList())
                : null;
        return new TrainerResponse(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getScheduledClasses() != null ? trainer.getScheduledClasses().size() : 0,
                classTypeDTOs
        );
    }

    public static TrainerResponse fromEntityWithoutCount(Trainer trainer) {
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
}
