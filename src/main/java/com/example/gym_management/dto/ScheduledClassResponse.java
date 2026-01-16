package com.example.gym_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledClassResponse {

    private Long id;
    private GymDTO gym;
    private ClassTypeDTO classType;
    private TrainerDTO trainer;
    private RoomDTO room;
    private LocalDateTime startTime;
    private Integer bookingCount;
    private Integer availableSpots;
}
