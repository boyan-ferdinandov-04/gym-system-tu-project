package com.example.gym_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerTimeOffResponse {

    private Long id;
    private Long trainerId;
    private String trainerName;
    private LocalDate date;
    private String reason;
}
