package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer availability slot")
public class TrainerAvailabilityResponse {

    private Long id;
    private Long trainerId;
    private String trainerName;

    @Schema(description = "Day of week for this slot")
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;
    private LocalTime endTime;
}
