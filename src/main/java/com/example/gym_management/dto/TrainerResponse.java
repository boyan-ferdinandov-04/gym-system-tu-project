package com.example.gym_management.dto;

import com.example.gym_management.entity.Trainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private Integer scheduledClassCount;

    public static TrainerResponse fromEntity(Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        return new TrainerResponse(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getSpecialization(),
                trainer.getScheduledClasses() != null ? trainer.getScheduledClasses().size() : 0
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
                trainer.getSpecialization(),
                null
        );
    }
}
