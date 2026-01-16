package com.example.gym_management.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledClassRequest {

    @NotNull(message = "Gym ID is required")
    private Long gymId;

    @NotNull(message = "Class type ID is required")
    private Long classTypeId;

    @NotNull(message = "Trainer ID is required")
    private Long trainerId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
}
