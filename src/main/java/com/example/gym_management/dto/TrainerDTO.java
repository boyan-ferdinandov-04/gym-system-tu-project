package com.example.gym_management.dto;

import com.example.gym_management.entity.Trainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDTO {

    private Long id;
    private String firstName;
    private String lastName;

    public static TrainerDTO fromEntity(Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        return new TrainerDTO(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName()
        );
    }
}
