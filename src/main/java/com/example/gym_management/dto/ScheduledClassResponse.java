package com.example.gym_management.dto;

import com.example.gym_management.entity.ScheduledClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledClassResponse {

    private Long id;
    private ClassTypeDTO classType;
    private TrainerDTO trainer;
    private RoomDTO room;
    private LocalDateTime startTime;
    private Integer bookingCount;
    private Integer availableSpots;

    public static ScheduledClassResponse fromEntity(ScheduledClass scheduledClass) {
        if (scheduledClass == null) {
            return null;
        }

        Integer bookingCount = scheduledClass.getBookings() != null ? scheduledClass.getBookings().size() : 0;
        Integer roomCapacity = scheduledClass.getRoom() != null ? scheduledClass.getRoom().getCapacity() : 0;
        Integer availableSpots = roomCapacity - bookingCount;

        return new ScheduledClassResponse(
                scheduledClass.getId(),
                ClassTypeDTO.fromEntity(scheduledClass.getClassType()),
                TrainerDTO.fromEntity(scheduledClass.getTrainer()),
                RoomDTO.fromEntity(scheduledClass.getRoom()),
                scheduledClass.getStartTime(),
                bookingCount,
                availableSpots
        );
    }
}
