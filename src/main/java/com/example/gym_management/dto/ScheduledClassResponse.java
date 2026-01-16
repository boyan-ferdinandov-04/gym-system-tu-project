package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Scheduled class details")
public class ScheduledClassResponse {

    private Long id;
    private GymDTO gym;
    private ClassTypeDTO classType;
    private TrainerDTO trainer;
    private RoomDTO room;
    private LocalDateTime startTime;

    @Schema(description = "Current number of bookings")
    private Integer bookingCount;

    @Schema(description = "Remaining spots for booking")
    private Integer availableSpots;
}
