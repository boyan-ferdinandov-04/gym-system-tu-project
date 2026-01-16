package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer time-off details")
public class TrainerTimeOffResponse {

    private Long id;
    private Long trainerId;
    private String trainerName;
    private LocalDate date;
    private String reason;
}
